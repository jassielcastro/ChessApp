package com.ajcm.chess.board

import com.ajcm.chess.Scope
import com.ajcm.chess.ext.equals
import com.ajcm.chess.ext.myEnemy
import com.ajcm.chess.ext.updateAndEmit
import com.ajcm.chess.ext.withPosition
import com.ajcm.chess.piece.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class Player(
    val color: Color,
    internal val isFake: Boolean = false,
    internal var canObserveMoves: Boolean = false
) : Scope by Scope.Impl(Dispatchers.Main) {

    lateinit var board: Board

    val status: MutableStateFlow<PlayerStatus> = MutableStateFlow(PlayerStatus.WAITING)
    val availablePieces: MutableStateFlow<List<Piece>> = MutableStateFlow(emptyList())
    val deadPieces: MutableStateFlow<List<Piece>> = MutableStateFlow(emptyList())
    val evolvePawn: MutableStateFlow<Pawn?> = MutableStateFlow(null)
    val kingStatus: MutableStateFlow<KingStatus> = MutableStateFlow(KingStatus.NONE)

    init {
        initScope()
        if (!isFake) {
            addBoardPieces()
            checkInitialMove()
        }
    }

    private fun checkInitialMove() = launch {
        if (color == Color.WHITE) {
            status.emit(PlayerStatus.MOVING)
        } else {
            status.emit(PlayerStatus.WAITING)
        }
    }

    internal fun existPieceOn(position: Position): Boolean {
        return availablePieces.value.any { it.position.value equals position }
    }

    internal fun getPieceFrom(position: Position): Piece? {
        return availablePieces.value.firstOrNull { it.position.value equals position }
    }

    internal fun isKingOn(position: Position): Boolean {
        return availablePieces.value.find { it.position.value equals position } is King
    }

    private fun getKing() = availablePieces.value.filterIsInstance<King>().first()

    internal fun validateKingStatus() = launch {
        validateKing(this@Player)
        validateKing(this@Player.myEnemy())
    }

    private suspend fun validateKing(player: Player) {
        if (player.isKingChecked()) {
            if (player.isCheckMate()) {
                player.myEnemy().kingStatus.emit(KingStatus.CHECK_MATE)
            } else {
                player.myEnemy().kingStatus.emit(KingStatus.CHECK)
            }
            return
        }
        player.myEnemy().kingStatus.emit(KingStatus.NONE)
    }

    internal fun isKingChecked(): Boolean {
        val forcedMoves = availablePieces.value.map { it.getCleanedMoves() }.flatten()
        val kingEnemy = myEnemy().getKing()
        return forcedMoves.contains(kingEnemy.position.value)
    }

    private fun isCheckMate(): Boolean {
        return myEnemy().availablePieces.value.asSequence().map { Pair(it, it.getCleanedMoves()) }
            .filterNot {
                it.second.isEmpty()
            }.map {
                it.second.map { newPosition ->
                    it.first.isValidMove(newPosition)
                }
            }.flatten().all { !it }
    }

    internal fun replace(oldPawn: Pawn, newPiece: Piece) = launch {
        val list = mutableListOf<Piece>()
        list.addAll(availablePieces.value)
        list.remove(oldPawn)
        list.add(newPiece)
        availablePieces.emit(list)
    }

    internal fun removePieceIn(position: Position) {
        val pieces = availablePieces.value.toMutableList()
        val piece = getPieceFrom(position)
        pieces.remove(piece)
        availablePieces.value = pieces
    }

    internal fun createFakePlayerBy(player: Player): Player {
        return Player(player.color, true).also { fake ->
            val pieces = mutableListOf<Piece>()
            player.availablePieces.value.map { piece ->
                pieces.add(piece.copyWith(fake).also {
                    it.position.value = piece.position.value
                })
            }
            fake.availablePieces.value = pieces.toList()
        }
    }

    private fun addBoardPieces() = launch {
        availablePieces.updateAndEmit {
            /*
             PawnV2
             */
            (1..Board.CELL_COUNT).forEach { x ->
                it.add(
                    Pawn(this@Player).withPosition(
                        Position(x, PositionConst.TWO),
                        Position(x, PositionConst.SEVEN)
                    )
                )
            }

            /*
             Bishops
             */
            it.add(
                Bishop(this@Player).withPosition(
                    Position(
                        PositionConst.THREE,
                        PositionConst.ONE
                    ), Position(PositionConst.THREE, PositionConst.EIGHT)
                )
            )
            it.add(
                Bishop(this@Player).withPosition(
                    Position(
                        PositionConst.SIX,
                        PositionConst.ONE
                    ), Position(PositionConst.SIX, PositionConst.EIGHT)
                )
            )

            /*
             Knights
             */
            it.add(
                Knight(this@Player).withPosition(
                    Position(PositionConst.TWO, PositionConst.ONE),
                    Position(PositionConst.TWO, PositionConst.EIGHT)
                )
            )
            it.add(
                Knight(this@Player).withPosition(
                    Position(
                        PositionConst.SEVEN,
                        PositionConst.ONE
                    ), Position(PositionConst.SEVEN, PositionConst.EIGHT)
                )
            )

            /*
             Rooks
             */
            it.add(
                Rook(this@Player).withPosition(
                    Position(PositionConst.ONE, PositionConst.ONE),
                    Position(PositionConst.ONE, PositionConst.EIGHT)
                )
            )
            it.add(
                Rook(this@Player).withPosition(
                    Position(
                        PositionConst.EIGHT,
                        PositionConst.ONE
                    ), Position(PositionConst.EIGHT, PositionConst.EIGHT)
                )
            )

            /*
             Queen
             */
            it.add(
                Queen(this@Player).withPosition(
                    Position(
                        PositionConst.FOUR,
                        PositionConst.ONE
                    ), Position(PositionConst.FOUR, PositionConst.EIGHT)
                )
            )

            /*
             King
             */
            it.add(
                King(this@Player).withPosition(
                    Position(PositionConst.FIVE, PositionConst.ONE),
                    Position(PositionConst.FIVE, PositionConst.EIGHT)
                )
            )
        }
    }

}
