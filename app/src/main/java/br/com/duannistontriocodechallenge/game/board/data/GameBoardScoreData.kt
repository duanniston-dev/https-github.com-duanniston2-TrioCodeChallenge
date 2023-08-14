package br.com.duannistontriocodechallenge.game.board.data

data class GameBoardScoreData(val robot1: Int = 0, val robot2: Int = 0, val noOneWin: Int = 0) {

    fun updateScoreRobot1(): GameBoardScoreData {
        return copy(robot1 = robot1 + 1)
    }

    fun updateScoreRobot2(): GameBoardScoreData {
        return copy(robot2 = robot2 + 1)
    }

    fun updateScoreNoOneWin(): GameBoardScoreData {
        return copy(noOneWin = noOneWin + 1)
    }
}