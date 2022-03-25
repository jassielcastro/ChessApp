package com.ajcm.chess.piece

import com.ajcm.chess.Scope
import com.ajcm.chess.board.*
import com.ajcm.chess.ext.clean
import com.ajcm.chess.ext.myEnemy
import com.ajcm.chess.ext.updateAndEmit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

abstract class Piece(
    internal open val player: Player,
    val position: MutableStateFlow<Position> = MutableStateFlow(Position())
) : Scope by Scope.Impl(Dispatchers.Main) {

    internal val id: String = UUID.randomUUID().toString()
    internal var isInitialMove: Boolean = true

    val color: Color by lazy {
        player.color
    }

    init {
        startScope()
        observePosition()
    }

    private fun observePosition() {
        if (this is Pawn) return
        launch {
            position.collect {
                if (!player.isFake && player.canObserveMoves) {
                    player.validateKingStatus()
                }
            }
        }
    }

    private fun startScope() = initScope()

    internal abstract fun getPossibleMoves(): List<Position>

    internal abstract fun copyWith(player: Player): Piece

    fun getAllPossibleMoves(): List<Position> {
        return if (player.status.value == PlayerStatus.MOVING) {
            val moves = getCleanedMoves()
            val validMoves = mutableListOf<Position>()
            for (position in moves) {
                if (isValidMove(position)) {
                    validMoves.add(position)
                }
            }
            validMoves.toList()
        } else {
            emptyList()
        }
    }

    internal fun getCleanedMoves(): List<Position> = player clean getPossibleMoves()

    internal open fun getSpecialMoves(): List<Position> = emptyList()

    fun updatePosition(newPosition: Position) {
        if (player.status.value == PlayerStatus.WAITING) return
        if (player.myEnemy().isKingOn(newPosition)) return
        player.canObserveMoves = !player.isFake
        checkIfExistEnemyToRemove(newPosition)
        updateMove(newPosition)
    }

    private fun checkIfExistEnemyToRemove(newPosition: Position) {
        with(player.myEnemy()) {
            val possibleEnemyPiece = getPieceFrom(newPosition)
            launch {
                if (possibleEnemyPiece != null && possibleEnemyPiece !is King) {
                    this@with.removePieceIn(newPosition)
                    this@with.deadPieces.updateAndEmit {
                        it.add(possibleEnemyPiece)
                    }
                }
            }
        }
    }

    internal fun isValidMove(newPosition: Position): Boolean {
        val me = player.createFakePlayerBy(player)
        val enemy = player.createFakePlayerBy(player.myEnemy())

        Board(
            if (me.color == Color.WHITE) me else enemy,
            if (me.color == Color.WHITE) enemy else me,
        )

        if (!enemy.isKingOn(newPosition)) {
            val mockedPiece = me.getPieceFrom(position.value)
            mockedPiece?.position?.value = newPosition
            if (enemy.existPieceOn(newPosition)) {
                enemy.removePieceIn(newPosition)
            }
        }

        return !enemy.isKingChecked()
    }

    private fun updateMove(newPosition: Position) = launch {
        isInitialMove = false
        player.myEnemy().status.emit(PlayerStatus.MOVING)
        player.status.emit(PlayerStatus.WAITING)
        position.value = newPosition
    }

}
