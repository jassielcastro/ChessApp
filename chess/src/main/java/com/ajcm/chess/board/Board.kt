package com.ajcm.chess.board

import com.ajcm.chess.piece.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Board(
    internal val whitePlayer: Player = Player(Color.WHITE),
    internal val blackPlayer: Player = Player(Color.BLACK),
    private val coroutineScope: CoroutineScope = MainScope()
) {

    companion object {
        const val CELL_COUNT = PositionConst.EIGHT
        const val COLUMN_COUNT = PositionConst.EIGHT
    }

    val positions: List<Position> by lazy {
        val mutableList = mutableListOf<Position>()
        for (positionX in 1..CELL_COUNT) {
            for (positionY in 1..COLUMN_COUNT) {
                mutableList.add(Position(positionX, positionY))
            }
        }
        mutableList.toList()
    }

    val whitePlayerStatus: StateFlow<PlayerStatus>
        get() = whitePlayer.status.asStateFlow()
    val blackPlayerStatus: StateFlow<PlayerStatus>
        get() = blackPlayer.status.asStateFlow()

    val whiteAvailablePieces: StateFlow<List<Piece>>
        get() = whitePlayer.availablePieces.asStateFlow()
    val blackAvailablePieces: StateFlow<List<Piece>>
        get() = blackPlayer.availablePieces.asStateFlow()

    val whiteDeadPieces: StateFlow<List<Piece>>
        get() = whitePlayer.deadPieces.asStateFlow()
    val blackDeadPieces: StateFlow<List<Piece>>
        get() = blackPlayer.deadPieces.asStateFlow()

    val whiteKingStatus: StateFlow<KingStatus>
        get() = whitePlayer.kingStatus.asStateFlow()
    val blackKingStatus: StateFlow<KingStatus>
        get() = blackPlayer.kingStatus.asStateFlow()

    val pawnToEvolve: StateFlow<Pawn?>
        get() = merge(whitePlayer.evolvePawn, blackPlayer.evolvePawn)
            .stateIn(coroutineScope, SharingStarted.Lazily, null)

    init {
        whitePlayer.board = this
        blackPlayer.board = this
    }

    fun transform(pawn: Pawn, transformation: PawnTransform) = with(pawn.player) {
        val newPiece = when (transformation) {
            PawnTransform.BISHOP -> Bishop(this)
            PawnTransform.KNIGHT -> Knight(this)
            PawnTransform.QUEEN -> Queen(this)
            PawnTransform.ROOK -> Rook(this)
        }.also {
            it.launch {
                it.position.emit(pawn.position.value)
            }
        }
        this.replace(pawn, newPiece)
    }

}
