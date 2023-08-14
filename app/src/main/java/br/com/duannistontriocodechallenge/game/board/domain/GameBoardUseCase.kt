package br.com.duannistontriocodechallenge.game.board.domain

import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardScoreData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardStateData
import br.com.duannistontriocodechallenge.game.board.data.Move
import br.com.duannistontriocodechallenge.game.board.data.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * UseCase responsible for managing the game board state, robots, prize, and game state.
 */
class GameBoardUseCase {

    // StateFlow to represent the current game board
    val boardFlow: MutableStateFlow<Array<Array<GameBoardAdapterItemData>>> =
        MutableStateFlow(emptyBoard())

    // StateFlow to represent the current state of the game
    val gameFlow: MutableStateFlow<GameBoardStateData> =
        MutableStateFlow(GameBoardStateData.WAITING)

    // StateFlow to represent the current score of the game
    val gameScoreFlow: MutableStateFlow<GameBoardScoreData> =
        MutableStateFlow(GameBoardScoreData(0, 0))

    /**
     * Creates an empty game board.
     */
    private fun emptyBoard() = Array(7) { column ->
        Array(7) { row ->
            GameBoardAdapterItemData(
                Position(column, row),
                GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
            )
        }
    }

    /**
     * Clears the game board and sets its state to CLEANED.
     */
    private fun clearBoard(): Flow<Unit> {
        return flow {
            val emptyBoard = emptyBoard()
            boardFlow.value = emptyBoard
            gameFlow.value = GameBoardStateData.CLEANED
            emit(Unit)
        }
    }

    /**
     * Places a prize on the game board at a random position.
     */
    private fun putPrize(): Flow<Unit> {
        return flow {
            // Get valid positions to place a prize.
            val listOfFilteredGameBoardAdapterItemData = filteredListToAddPrize()

            // Select a random position.
            val randomGameBoardAdapterItemData =
                listOfFilteredGameBoardAdapterItemData[Random.nextInt(
                    listOfFilteredGameBoardAdapterItemData.size
                )]

            val newBoardValues = boardFlow.value.clone()

            // Place the prize at the chosen position.
            newBoardValues[randomGameBoardAdapterItemData.position.x][randomGameBoardAdapterItemData.position.y].gameBoardAdapterType =
                GameBoardAdapterItemData.GameBoardAdapterType.PRIZE

            boardFlow.value = newBoardValues
            gameFlow.value = GameBoardStateData.ADDED_PRIZE
            emit(Unit)
        }
    }

    /**
     * Filters out positions on the board that are occupied by robots.
     */
    private fun filteredListToAddPrize() =
        boardFlow.value.clone().flatMap { it.toList() }.filterNot { item ->
            filterToAddPrize(item)
        }.toList().shuffled()

    /**
     * Checks if a position is occupied by a robot.
     *
     * @param item The board item to check.
     * @return Returns `true` if the position is occupied by a robot, otherwise `false`.
     */
    private fun filterToAddPrize(item: GameBoardAdapterItemData) =
        ((item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT) ||
                (item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT)
                )

    /**
     * Places both robots at their starting positions on the board.
     */
    private fun putRobots(): Flow<Unit> {

        return flow {
            val newBoard = boardFlow.value.clone()

            // Set Robot1 and Robot2 at their start positions.
            setRobot1AtStartPosition(newBoard)
            setRobot2AtStartPosition(newBoard)

            boardFlow.value = newBoard
            gameFlow.value = GameBoardStateData.ADDED_ROBOTS
            emit(Unit)
        }


    }

    /**
     * Sets Robot2 at its starting position.
     *
     * @param newBoard The board on which to set Robot2's position.
     */
    private fun setRobot2AtStartPosition(newBoard: Array<Array<GameBoardAdapterItemData>>) {
        newBoard[6][6] = GameBoardAdapterItemData(
            Position(6, 6),
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
        )
    }

    /**
     * Sets Robot1 at its starting position.
     *
     * @param newBoard The board on which to set Robot1's position.
     */
    private fun setRobot1AtStartPosition(newBoard: Array<Array<GameBoardAdapterItemData>>) {
        newBoard[0][0] = GameBoardAdapterItemData(
            Position(0, 0),
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
        )
    }

    /**
     * Starts the game, initiating board setup and robot movements.
     */
    fun startGame(): Flow<Unit> {
        return flowOf(
            clearBoard(),
            putRobots(),
            putPrize(),
            moveRobot()
        ).flattenConcat()
    }

    /**
     * Handles robot movement on the board, checking for valid moves, and updating the board accordingly.
     */
    private fun moveRobot(): Flow<Unit> {
        return flow {
            // Initial delay to improve UX
            delay(TimeUnit.SECONDS.toMillis(1))

            while (true) {
                // Game is currently running.
                gameFlow.value = GameBoardStateData.RUNNING

                // Get the current position of Robot1 and determine its valid moves.
                val currentRobot1 = getCurrentRobot1()
                val validMovesForRobot1 = findValidMovesForRobots(currentRobot1)

                // Execute Robot1's movement.
                moveRobot(validMovesForRobot1, currentRobot1)
                delay(500)

                // Get the current position of Robot2 and determine its valid moves.
                val currentRobot2 = getCurrentRobot2()
                val movesRobot2 = findValidMovesForRobots(currentRobot2)

                // Execute Robot2's movement.
                moveRobot(movesRobot2, currentRobot2)

                // If neither robot can move, finish the game and reset the board.
                if (validMovesForRobot1.isEmpty() && movesRobot2.isEmpty()) {
                    gameScoreFlow.value = gameScoreFlow.value.updateScoreNoOneWin()
                    gameFlow.value = GameBoardStateData.GAME_FINISHED
                    clearBoard().collect()
                    putRobots().collect()
                    putPrize().collect()
                    emit(Unit)
                }
                delay(500)
            }
        }
    }

    /**
     * Determines the valid moves a robot can make from its current position.
     *
     * @param currentRobot The current robot for which to determine valid moves.
     * @return A list of valid moves for the robot.
     */
    private fun findValidMovesForRobots(currentRobot: GameBoardAdapterItemData): List<Move> {
        val movesRobot = Move.values().filter { move ->

            val newPosition = currentRobot.copy().position.move(move)
            return@filter isPositionValid(newPosition) && robotCanFollowThisLine(
                newPosition.x,
                newPosition.y
            )

        }
        return movesRobot
    }

    /**
     * Determines a robot's current position and moves it based on its valid moves.
     *
     * @param validMovesForRobot A list of valid moves the robot can make.
     * @param robot The current position of the robot.
     */
    private suspend fun FlowCollector<Unit>.moveRobot(
        validMovesForRobot: List<Move>,
        robot: GameBoardAdapterItemData
    ) {
        if (validMovesForRobot.isNotEmpty()) {
            val newBoard = boardFlow.value.clone()
            val robotClone = robot.copy()

            newBoard[robotClone.position.x][robotClone.position.y].gameBoardAdapterType =
                setGameBoardItemToCurrentLineType(robotClone)

            val moveForRobot = validMovesForRobot.random()
            val newRobotPosition = robotClone.position.move(moveForRobot)
            val newCurrentRobot = robotClone.copy(position = newRobotPosition)

            if (newBoard[newCurrentRobot.position.x][newCurrentRobot.position.y].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE) {
                updateScore(robotClone)
                gameFlow.value = GameBoardStateData.GAME_FINISHED
                clearBoard().collect()
                putRobots().collect()
                putPrize().collect()
                emit(Unit)
            } else {
                newBoard[newCurrentRobot.position.x][newCurrentRobot.position.y].gameBoardAdapterType =
                    setGameBoardItemToCurrentType(robotClone, robot)
                boardFlow.value = newBoard
            }

        }
    }


    /**
     * Updates the game score based on which robot is moving.
     *
     * @param robotClone Current data of the robot being moved.
     */
    private fun updateScore(robotClone: GameBoardAdapterItemData) {
        gameScoreFlow.value = when (robotClone.gameBoardAdapterType) {
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT -> {
                gameScoreFlow.value.updateScoreRobot1()
            }

            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT -> {
                gameScoreFlow.value.updateScoreRobot2()
            }

            else -> {
                GameBoardScoreData()
            }
        }
    }

    /**
     * Sets the board item type to represent the robot's current state.
     *
     * @param robotClone The robot data to check.
     * @param robot Original data of the robot.
     * @return The corresponding type based on the robot's state.
     */
    private fun setGameBoardItemToCurrentType(
        robotClone: GameBoardAdapterItemData,
        robot: GameBoardAdapterItemData
    ) = when (robotClone.gameBoardAdapterType) {
        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT -> {
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
        }

        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT -> {
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
        }

        else -> {
            robot.gameBoardAdapterType
        }
    }

    /**
     * Sets the board item type to represent the path or line taken by the robot.
     *
     * @param robotClone The robot data to check.
     * @return The corresponding type based on the robot's line.
     */
    private fun setGameBoardItemToCurrentLineType(robotClone: GameBoardAdapterItemData) =
        when (robotClone.gameBoardAdapterType) {
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT -> {
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_LINE
            }

            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT -> {
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_LINE
            }

            else -> {
                robotClone.gameBoardAdapterType
            }
        }

    /**
     * Retrieves the current data of Robot 1 from the game board.
     *
     * @return Data representing Robot 1's current state.
     */
    private fun getCurrentRobot1(): GameBoardAdapterItemData {
        val currentRobot1 = boardFlow.value.clone().flatMap { it.toList() }.find {
            it.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
        }!!
        return currentRobot1
    }

    /**
     * Retrieves the current data of Robot 2 from the game board.
     *
     * @return Data representing Robot 2's current state.
     */
    private fun getCurrentRobot2(): GameBoardAdapterItemData {
        val currentRobot2 = boardFlow.value.clone().flatMap { it.toList() }.find {
            it.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
        }!!
        return currentRobot2
    }

    /**
     * Checks if a given position is within the valid boundaries of the game board.
     *
     * @param position The position to check.
     * @return `true` if the position is within the board boundaries, `false` otherwise.
     */
    private fun isPositionValid(position: Position): Boolean {
        return position.x in 0..6 && position.y in 0..6
    }

    /**
     * Determines if a robot can move to a given position based on the item type present there.
     *
     * @param column The x-coordinate of the position.
     * @param row The y-coordinate of the position.
     * @return `true` if the robot can move to that position, `false` otherwise.
     */
    private fun robotCanFollowThisLine(column: Int, row: Int): Boolean {
        return (boardFlow.value[column][row].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.EMPTY)
                || (boardFlow.value[column][row].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE)
    }

}