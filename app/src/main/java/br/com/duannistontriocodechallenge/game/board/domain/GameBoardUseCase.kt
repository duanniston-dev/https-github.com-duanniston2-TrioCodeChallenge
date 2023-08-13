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

class GameBoardUseCase {


    val boardFlow: MutableStateFlow<Array<Array<GameBoardAdapterItemData>>> =
        MutableStateFlow(emptyBoard())

    val gameFlow: MutableStateFlow<GameBoardStateData> =
        MutableStateFlow(GameBoardStateData.WAITING)

    val gameScoreFlow: MutableStateFlow<GameBoardScoreData> =
        MutableStateFlow(GameBoardScoreData(0, 0))

    private fun emptyBoard() = Array(7) { column ->
        Array(7) { row ->
            GameBoardAdapterItemData(
                Position(column, row),
                GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
            )
        }
    }

    private fun clearBoard(): Flow<Unit> {
        return flow {
            val emptyBoard = emptyBoard()
            boardFlow.value = emptyBoard
            gameFlow.value = GameBoardStateData.CLEANED
            emit(Unit)
        }
    }

    private fun putPrize(): Flow<Unit> {

        return flow {
            val listOfFilteredGameBoardAdapterItemData = filteredListToAddPrize()

            val randomGameBoardAdapterItemData =
                listOfFilteredGameBoardAdapterItemData[Random.nextInt(
                    listOfFilteredGameBoardAdapterItemData.size
                )]

            val newBoardValues = boardFlow.value.clone()

            newBoardValues[randomGameBoardAdapterItemData.position.x][randomGameBoardAdapterItemData.position.y].gameBoardAdapterType =
                GameBoardAdapterItemData.GameBoardAdapterType.PRIZE

            boardFlow.value = newBoardValues
            gameFlow.value = GameBoardStateData.ADDED_PRIZE
            emit(Unit)
        }

    }

    private fun filteredListToAddPrize() =
        boardFlow.value.clone().flatMap { it.toList() }.filterNot { item ->
            filterToAddPrize(item)
        }.toList().shuffled()

    private fun filterToAddPrize(item: GameBoardAdapterItemData) =
        ((item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT) ||
                (item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT)
                )

    private fun putRobots(): Flow<Unit> {

        return flow {
            val newBoard = boardFlow.value.clone()

            setRobot1AtStartPosition(newBoard)
            setRobot2AtStartPosition(newBoard)

            boardFlow.value = newBoard
            gameFlow.value = GameBoardStateData.ADDED_ROBOTS
            emit(Unit)
        }


    }

    private fun setRobot2AtStartPosition(newBoard: Array<Array<GameBoardAdapterItemData>>) {
        newBoard[6][6] = GameBoardAdapterItemData(
            Position(6, 6),
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
        )
    }

    private fun setRobot1AtStartPosition(newBoard: Array<Array<GameBoardAdapterItemData>>) {
        newBoard[0][0] = GameBoardAdapterItemData(
            Position(0, 0),
            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
        )
    }


    fun startGame(): Flow<Unit> {
        return flowOf(
            clearBoard(),
            putRobots(),
            putPrize(),
            moveRobot()
        ).flattenConcat()
    }

    private fun moveRobot(): Flow<Unit> {
        return flow {

            delay(TimeUnit.SECONDS.toMillis(1))

            while (true) {
                gameFlow.value = GameBoardStateData.RUNNING

                val currentRobot1 = getCurrentRobot1()

                val validMovesForRobot1 = findValidMovesForRobots(currentRobot1)

                moveRobot(validMovesForRobot1,currentRobot1)

                delay(500)

                //Robot 2
                val currentRobot2 = getCurrentRobot2()

                val movesRobot2 = findValidMovesForRobots(currentRobot2)
                moveRobot(movesRobot2,currentRobot2)

                if (validMovesForRobot1.isEmpty() && movesRobot2.isEmpty()) {
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


    private fun getCurrentRobot1(): GameBoardAdapterItemData {
        val currentRobot1 = boardFlow.value.clone().flatMap { it.toList() }.find {
            it.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
        }!!
        return currentRobot1
    }

    private fun getCurrentRobot2(): GameBoardAdapterItemData {
        val currentRobot2 = boardFlow.value.clone().flatMap { it.toList() }.find {
            it.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
        }!!
        return currentRobot2
    }

    private fun isPositionValid(position: Position): Boolean {
        return position.x in 0..6 && position.y in 0..6
    }

    private fun robotCanFollowThisLine(column: Int, row: Int): Boolean {
        return (boardFlow.value[column][row].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.EMPTY)
                || (boardFlow.value[column][row].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE)
    }

}