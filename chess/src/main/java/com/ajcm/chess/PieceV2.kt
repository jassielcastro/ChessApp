package com.ajcm.chess

import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Position
import com.ajcm.chess.board.X
import com.ajcm.chess.board.Y
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Status {
    CHECKED, UNCHECKED
}

interface Piece {

    val player: Player
    var isInitialMove: Boolean
    val position: MutableStateFlow<Position>
    val status: MutableStateFlow<Status>
    val possibleMoves: List<Position>

    // Only for Pawn
    val evolvePiece: MutableStateFlow<Boolean>

    fun updatePosition(newPosition: Position)
    fun getSpecialMoves(): List<Position>
    fun getAllPossibleMoves(): List<Position>

    class State(
        override val player: Player,
        override val position: MutableStateFlow<Position> = MutableStateFlow(Position())
    ) : Piece, Scope by Scope.Impl(Dispatchers.Main) {

        override var isInitialMove: Boolean = false
        override val possibleMoves: List<Position>
            get() {
                return if (player.isMoving.value) {
                    player clean getAllPossibleMoves()
                } else {
                    emptyList()
                }
            }
        override val status: MutableStateFlow<Status> = MutableStateFlow(Status.UNCHECKED)
        override val evolvePiece: MutableStateFlow<Boolean> = MutableStateFlow(false)

        init {
            initScope()
            checkPiecesStatus()
        }

        private fun checkPiecesStatus() = launch {
            player.isMoving.collect { moving ->
                if (moving) {
                    val enemy = player.myEnemy()
                    val checked = enemy.availablePieces
                        .value
                        .map { it.position.value }
                        .contains(position.value)

                    status.emit(
                        if (checked) {
                            Status.CHECKED
                        } else {
                            Status.UNCHECKED
                        }
                    )
                }
            }
        }

        override fun updatePosition(newPosition: Position) {
            if (!player.isMoving.value) return
            launch {
                isInitialMove = false
                player.isMoving.emit(false)
                with(player.myEnemy()) {
                    isMoving.emit(true)
                    getPieceFrom(newPosition)?.let { piece ->
                        if (piece !is King) {
                            availablePieces.updateAndEmit {
                                it.remove(piece)
                            }
                            deadPieces.updateAndEmit {
                                it.add(piece)
                            }
                        }
                    }
                }
                position.emit(newPosition)
                player.availablePieces.emit(player.availablePieces.value)
            }
        }

        override fun getSpecialMoves(): List<Position> = emptyList()

        override fun getAllPossibleMoves(): List<Position> = emptyList()

    }

}

suspend inline fun MutableStateFlow<List<Piece>>.updateAndEmit(crossinline action: suspend (MutableList<Piece>) -> Unit) {
    val list = mutableListOf<Piece>()
    update {
        action(list)
        this.value + list.toList()
    }
}

data class Player(
    val color: Color
) : Scope by Scope.Impl(Dispatchers.Main) {

    lateinit var board: Board

    var isMoving: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val availablePieces: MutableStateFlow<List<Piece>> = MutableStateFlow(emptyList())
    val deadPieces: MutableStateFlow<List<Piece>> = MutableStateFlow(emptyList())

    init {
        initScope()
        addPawns()
        addBishops()
        addKnights()
        addRooks()
        addQueen()
        addKing()
        checkInitialMove()
    }

    private fun checkInitialMove() = launch {
        if (color == Color.WHITE) {
            isMoving.emit(true)
        }
    }

    fun existPieceOn(position: Position): Boolean {
        return availablePieces.value.map { it.position.value }.contains(position)
    }

    fun getPieceFrom(position: Position): Piece? {
        return availablePieces.value.firstOrNull { it.position.value == position }
    }

    fun isKingOn(position: Position): Boolean {
        return availablePieces.value.find { it.position.value == position } is King
    }

    private fun addPawns() = launch {
        (1..8).forEach { x ->
            availablePieces.updateAndEmit {
                it.add(Pawn(this@Player).withPosition({ Position(x, 2) }, { Position(x, 7) }))
            }
        }
    }

    private fun addBishops() = launch {
        availablePieces.updateAndEmit {
            it.add(Bishop(this@Player).withPosition({ Position(3, 1) }, { Position(3, 8) }))
            it.add(Bishop(this@Player).withPosition({ Position(6, 1) }, { Position(6, 8) }))
        }
    }

    private fun addKnights() = launch {
        availablePieces.updateAndEmit {
            it.add(Knight(this@Player).withPosition({ Position(2, 1) }, { Position(2, 8) }))
            it.add(Knight(this@Player).withPosition({ Position(7, 1) }, { Position(7, 8) }))
        }
    }

    private fun addRooks() = launch {
        availablePieces.updateAndEmit {
            it.add(Rook(this@Player).withPosition({ Position(1, 1) }, { Position(1, 8) }))
            it.add(Rook(this@Player).withPosition({ Position(8, 1) }, { Position(8, 8) }))
        }
    }

    private fun addQueen() = launch {
        availablePieces.updateAndEmit {
            it.add(Queen(this@Player).withPosition({ Position(4, 1) }, { Position(4, 8) }))
        }
    }

    private fun addKing() = launch {
        availablePieces.updateAndEmit {
            it.add(King(this@Player).withPosition({ Position(5, 1) }, { Position(5, 8) }))
        }
    }
}

internal val diagonalMoves: List<Position> by lazy {
    listOf(Position(-1, -1), Position(-1, 1), Position(1, -1), Position(1, 1))
}
internal val linealMoves: List<Position> by lazy {
    listOf(Position(-1, 0), Position(0, 1), Position(1, 0), Position(0, -1))
}

fun Position.next(x: X, y: Y): Position {
    return Position(this.x + x, this.y + y)
}

suspend fun <P : Piece> P.withPosition(
    whitePosition: () -> Position,
    blackPosition: () -> Position
): P {
    this.position.emit(if (player.color == Color.WHITE) whitePosition() else blackPosition())
    return this
}

fun Piece.getMovesBy(direction: List<Position>): List<Position> {
    val possibleMoves = mutableListOf<Position>()
    for (d in direction) {
        for (p in 1..Board.CELL_COUNT) {
            val newPosition = position.value.next(d.x * p, d.y * p)
            if (!player.existPieceOn(newPosition)) {
                possibleMoves.add(newPosition)
                val enemy = player.myEnemy()
                if (enemy.existPieceOn(newPosition)) {
                    break
                }
            } else {
                break
            }
        }
    }
    return possibleMoves
}

fun Player.myEnemy(): Player = with(this.board) {
    return if (firstPlayer == this@myEnemy) firstPlayer else secondPlayer
}

infix fun Player.clean(possibleMoves: List<Position>): List<Position> {
    val allPossibleEnemyMoves = myEnemy().availablePieces.value.flatMap { it.getAllPossibleMoves() }
    return possibleMoves.filter {
        !allPossibleEnemyMoves.contains(it) && board.positions.contains(it)
    }
}

class Game(val board: Board) {



}

data class Board(
    val firstPlayer: Player = Player(Color.WHITE),
    val secondPlayer: Player = Player(Color.BLACK)
) {

    companion object {
        const val CELL_COUNT = 8
        const val COLUMN_COUNT = 8
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

    init {
        firstPlayer.board = this
        secondPlayer.board = this
    }

}

class Pawn(
    override val player: Player
) : Piece by Piece.State(player),
    Scope by Scope.Impl(Dispatchers.Main) {

    init {
        initScope()
        launch {
            position.collect {
                val enemyYBaseLine = if (player.color == Color.WHITE) 8 else 1
                if (position.value.y == enemyYBaseLine) {
                    evolvePiece.emit(true)
                }
            }
        }
    }

    override fun getAllPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        val enemy = player.myEnemy()
        val direction = getDirection()

        val p = position.value

        if (!enemy.existPieceOn(p.next(0, 1 * direction))) {
            moves.add(p.next(0, 1 * direction))
        }
        if (enemy.existPieceOn(p.next(-1, 1 * direction))
            && !enemy.isKingOn(p.next(-1, 1 * direction))
        ) {
            moves.add(p.next(-1, 1 * direction))
        }
        if (enemy.existPieceOn(p.next(1, 1 * direction))
            && !enemy.isKingOn(p.next(1, 1 * direction))
        ) {
            moves.add(p.next(1, 1 * direction))
        }

        moves.addAll(getSpecialMoves())

        return moves.toList()
    }

    override fun getSpecialMoves(): List<Position> {
        val enemy = player.myEnemy()
        val direction = getDirection()

        if (isInitialMove && !enemy.existPieceOn(position.value.next(0, 1 * direction))
            && !enemy.existPieceOn(position.value.next(0, 2 * direction))
        ) {
            return listOf(position.value.next(0, 2 * direction))
        }

        return emptyList()
    }

    private fun getDirection() = if (player.color == Color.WHITE) 1 else -1

}

class Bishop(
    override val player: Player
) : Piece by Piece.State(player) {

    override fun getAllPossibleMoves(): List<Position> {
        return getMovesBy(diagonalMoves)
    }

}

class Knight(
    override val player: Player
) : Piece by Piece.State(player) {

    override fun getAllPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        for (p in -2..2) {
            val y = if (p % 2 != 0) 2 else 1
            if (p != 0) {
                moves.add(position.value.next(p, y))
                moves.add(position.value.next(p, -1 * y))
            }
        }
        return moves.toList()
    }

}

class Rook(
    override val player: Player
) : Piece by Piece.State(player) {

    override fun getAllPossibleMoves(): List<Position> {
        return getMovesBy(linealMoves)
    }

}

class Queen(
    override val player: Player
) : Piece by Piece.State(player) {

    override fun getAllPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        moves.addAll(getMovesBy(diagonalMoves))
        moves.addAll(getMovesBy(linealMoves))
        return moves.toList()
    }

}

class King(
    override val player: Player
) : Piece by Piece.State(player),
    Scope by Scope.Impl(Dispatchers.Main) {

    init {
        initScope()
        launch {
            position.collect {
                if (it.x == 7) {
                    moveIfCastling()
                }
            }
        }
    }

    private fun moveIfCastling() = launch {
        val y = if (player.color == Color.WHITE) 1 else 8
        val rook = player.availablePieces.value.filterIsInstance<Rook>()
            .firstOrNull { position.value == Position(8, y) }
        rook?.position?.emit(Position(6, y))
    }

    override fun getAllPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        val directions = mutableListOf<Position>()
        directions.addAll(diagonalMoves)
        directions.addAll(linealMoves)
        for (direction in directions) {
            moves.add(position.value.next(direction.x, direction.y))
        }
        moves.addAll(getSpecialMoves())
        return moves.toList()
    }

    override fun getSpecialMoves(): List<Position> {
        if (!isInitialMove) {
            return emptyList()
        }

        val rook = getSpecialRook()

        if (rook == null || !rook.isInitialMove) {
            return emptyList()
        }

        if (player.existPieceOn(Position(6, getSpecialY()))
            || player.existPieceOn(Position(7, getSpecialY()))
        ) {
            return emptyList()
        }

        return listOf(Position(7, getSpecialY())) // Castling movement
    }

    private fun getSpecialRook(): Rook? {
        return player.availablePieces.value.filterIsInstance<Rook>().firstOrNull {
            it.position.value.x == 8
        }
    }

    private fun getSpecialY(): Int = if (player.color == Color.WHITE) 1 else 8

}
