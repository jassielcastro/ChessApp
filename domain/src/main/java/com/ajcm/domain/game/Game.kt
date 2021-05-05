package com.ajcm.domain.game

import com.ajcm.domain.players.Player
import java.lang.IllegalArgumentException

class Game(val playerOne: Player,  val playerTwo: Player) {
    init {
        if (playerOne.isMoving && playerTwo.isMoving) {
            throw IllegalArgumentException("Just one Player can move at time")
        }
        if (playerOne.color == playerTwo.color) {
            throw IllegalArgumentException("Select a different color by each Player")
        }
    }
}
