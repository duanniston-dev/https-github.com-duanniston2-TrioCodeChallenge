package br.com.duannistontriocodechallenge.game.board.domain

import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardScoreData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardStateData
import br.com.duannistontriocodechallenge.game.board.data.Move
import br.com.duannistontriocodechallenge.game.board.data.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GameBoardUseCase {


    val boardFlow: MutableStateFlow<Array<Array<GameBoardAdapterItemData>>> =
        MutableStateFlow(Array(7) { column ->
            Array(7) { row ->
                GameBoardAdapterItemData(
                    Position(column, row),
                    GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
                )
            }
        })

    val gameFlow: MutableStateFlow<GameBoardStateData> =
        MutableStateFlow(GameBoardStateData.WAITING)

    val gameScoreFlow: MutableStateFlow<GameBoardScoreData> =
        MutableStateFlow(GameBoardScoreData(0, 0))

    private fun clearBoard(): Flow<Unit> {
        return flow {
            val resetBoard = Array(7) { column ->
                Array(7) { row ->
                    GameBoardAdapterItemData(
                        Position(column, row),
                        GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
                    )
                }
            }
            boardFlow.value = resetBoard
            emit(Unit)
            gameFlow.value = GameBoardStateData.CLEANED
        }
    }

    private fun putPrize(): Flow<Unit> {

        return flow {
            val listFiltered = boardFlow.value.clone().flatMap { it.toList() }.filterNot { item ->
                ((item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT) ||
                        (item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT)
                        )

            }.toList().shuffled()


            val randomItem = listFiltered[Random.nextInt(listFiltered.size)]

            val newBoardValues = boardFlow.value.clone()

            newBoardValues[randomItem.position.x][randomItem.position.y].gameBoardAdapterType =
                GameBoardAdapterItemData.GameBoardAdapterType.PRIZE
            emit(Unit)
            boardFlow.value = newBoardValues
            gameFlow.value = GameBoardStateData.ADDED_PRIZE
        }

    }

    private fun putRobots(): Flow<Unit> {

        return flow {
            val newBoard = boardFlow.value.clone()

            newBoard[0][0] = GameBoardAdapterItemData(
                Position(0, 0),
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
            )

            newBoard[6][6] = GameBoardAdapterItemData(
                Position(6, 6),
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
            )
            emit(Unit)
            boardFlow.value = newBoard
            gameFlow.value = GameBoardStateData.ADDED_ROBOTS
        }


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
                emit(Unit)
                gameFlow.value = GameBoardStateData.RUNNING

                val currentRobot1 = getCurrentRobot1()

                val movesRobot1 = Move.values().filter { move ->

                    val newPosition = currentRobot1.copy().position.move(move)
                    return@filter isPositionValid(newPosition) && robotCanFollowThisLine(
                        newPosition.x,
                        newPosition.y
                    )

                }

                if (movesRobot1.isNotEmpty()) {
                    val newBoard1 = boardFlow.value.clone()

                    newBoard1[currentRobot1.position.x][currentRobot1.position.y].gameBoardAdapterType =
                        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_LINE

                    val moveForRobot1 = movesRobot1.random()
                    val newRobot1Position = currentRobot1.position.move(moveForRobot1)
                    val newCurrentRobot1 = currentRobot1.copy(position = newRobot1Position)

                    if (newBoard1[newCurrentRobot1.position.x][newCurrentRobot1.position.y].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE) {
                        emit(Unit)
                        gameScoreFlow.value = gameScoreFlow.value.updateScoreRobot1()
                        gameFlow.value = GameBoardStateData.GAME_FINISHED
                        clearBoard().collect()
                        putRobots().collect()
                        putPrize().collect()
                    } else {
                        newBoard1[newCurrentRobot1.position.x][newCurrentRobot1.position.y].gameBoardAdapterType =
                            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
                        boardFlow.value = newBoard1
                    }

                }

                delay(500)

                //Robot 2
                val currentRobot2 = getCurrentRobot2()

                val movesRobot2 = Move.values().filter { move ->

                    val newPosition = currentRobot2.copy().position.move(move)
                    return@filter isPositionValid(newPosition) && robotCanFollowThisLine(
                        newPosition.x,
                        newPosition.y
                    )

                }

                if (movesRobot2.isNotEmpty()) {
                    val newBoard2 = boardFlow.value.clone()

                    newBoard2[currentRobot2.position.x][currentRobot2.position.y].gameBoardAdapterType =
                        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_LINE

                    val moveForRobot2 = movesRobot2.random()
                    val newRobot2Position = currentRobot2.position.move(moveForRobot2)
                    val newCurrentRobot2 = currentRobot2.copy(position = newRobot2Position)

                    if (newBoard2[newCurrentRobot2.position.x][newCurrentRobot2.position.y].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE) {
                        emit(Unit)
                        gameScoreFlow.value = gameScoreFlow.value.updateScoreRobot2()
                        gameFlow.value = GameBoardStateData.GAME_FINISHED
                        clearBoard().collect()
                        putRobots().collect()
                        putPrize().collect()
                    } else {
                        newBoard2[newCurrentRobot2.position.x][newCurrentRobot2.position.y].gameBoardAdapterType =
                            GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT

                        boardFlow.value = newBoard2
                    }

                }
                if (movesRobot1.isEmpty() && movesRobot2.isEmpty()) {
                    emit(Unit)
                    gameFlow.value = GameBoardStateData.GAME_FINISHED
                    clearBoard().collect()
                    putRobots().collect()
                    putPrize().collect()
                }
                delay(500)
            }
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