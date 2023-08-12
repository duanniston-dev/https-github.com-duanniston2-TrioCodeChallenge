package br.com.duannistontriocodechallenge.game.board.data

data class GameBoardAdapterItemData(
    var position: Position,
    var gameBoardAdapterType: GameBoardAdapterType
) {
    enum class GameBoardAdapterType {
        EMPTY,
        PRIZE,
        ROBOT_1_CURRENT,
        ROBOT_2_CURRENT,
        ROBOT_1_LINE,
        ROBOT_2_LINE
    }
}
