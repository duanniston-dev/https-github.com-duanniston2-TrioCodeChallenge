package br.com.duannistontriocodechallenge.game.board.data

data class Position(val x: Int, val y: Int) {
    fun move(direction: Move): Position {
        return when (direction) {
            Move.UP -> copy(y = y - 1)
            Move.DOWN -> copy(y = y + 1)
            Move.LEFT -> copy(x = x - 1)
            Move.RIGHT -> copy(x = x + 1)
        }
    }
}

