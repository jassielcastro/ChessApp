# ChessApp - An Android Chess Library!

This library contains the essential functionality of the Chess game, so you will be able to create your own interface using the logic from this SDK in a very simple way.

[![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=14)

![Header](./docs/chess-app-sdk.png)

## General

This library offers you a wide variety of methods with which you can create your own game, the way of implementation is very simple and the use is much easier.

## Install

In order to use any library published within GitHub packages, you will need to do the following steps described [here](https://docs.github.com/es/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token) to obtain your personal token.

You will have to create your token by requesting the following permissions:

![Permissions](./docs/gthub_permissions.png)

Once your token is created (remember to save it and never share it) we will create the following files in your project:

1. Add the following code in your module where the SDK will be used:

```groovy
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/jassielcastro/ChessApp")
        credentials {
            username = YOUR_GITHUB_USER
            password = YOUR_GITHUB_TOKEN
        }
    }
}
```

2. Then add this dependency to your module where the SDK will be used:

```groovy
dependencies {
    implementation 'com.ajcm.chess:core:$current-version'
}
```

# Usage

As we know, chess games are composed of 2 parts:
1. Two players (White and Black) 2.
2. A board

So it will be necessary to create these objects before passing them to our game.

```kotlin
private val playerOne: Player = Player(Color.WHITE)
private val playerTwo: Player = Player(Color.BLACK)
```

These players can be created depending on the needs of the game, but just keep in mind that you need a player with white pieces and another with black pieces, the pieces are generated automatically, just indicate the color.

Now, the board is created as follows:

```kotlin
private val board: Board = Board()
```

And finally, we can create our game:

```kotlin
private val game: Game = GameSource(playerOne, playerTwo, board)
```

Having our game ready, these are the functions that can be performed:

1. Each player has a list of available pieces and with each piece you can obtain their possible moves:

```kotlin
val piece = playerOne.availablePieces[0] // obtaining the first piece
val moves = piece.getPossibleMoves(playerOne, game)
```

2. Obtain the enemy of player:

```kotlin
game.enemyOf(player)
```

3. Validate if the next move can be performed if the King is not in that position:

```kotlin
game.isKingEnemyOn(position, enemyPlayer)
```

4. Update or make a movement:

```kotlin
game.updateMovement(piece, newPosition, player)
```

5. If the moved piece is a Pawn, you can validate if the Pawn can convert to another piece:

```kotlin
piece.canConvertPiece()
```

6. Validate if the King is checked:

```kotlin
game.isKingCheckedOf(player, playerEnemy)
```

7. Validate that there are still movements available [Jake-Mate].:

```kotlin
game.hasNoOwnMovements(player, playerEnemy)
```

8. Know who is moving:

```kotlin
game.whoIsMoving()
```

9. Convert the Pawn to a new selected Piece:

```kotlin
game.convert(pawnPiece, PawnTransform.QUEEN)
```

10. Update the turn:

```kotlin
game.updateTurn()
```

## Example

```kotlin
private fun makeMovement(piece: Piece, newPosition: Position, player: Player) {
    if (!game.isKingEnemyOn(newPosition, game.enemyOf(player))) {
         game.updateMovement(piece, newPosition, player)
         if (piece.canConvertPiece()) {
            showAvailablePawnTransformations()
         }
    }
}
```

```kotlin
private fun checkKingStatus(player: Player) {
    if (game.isKingCheckedOf(player, game.enemyOf(player))) {
        showKingChecked()
        if (game.hasNoOwnMovements(player, game.enemyOf(player))) {
            showJakeMate()
            return
        }
    }
    game.updateTurn()
}
```

# Contributing & Reporting Issues

[Please read this if you're reporting an issue, or thinking of contributing!](./CONTRIBUTING.md)

## Licence

See the [LICENSE](LICENSE.md) file for license rights and limitations (MIT).

Copyright 2021 AJCM.
