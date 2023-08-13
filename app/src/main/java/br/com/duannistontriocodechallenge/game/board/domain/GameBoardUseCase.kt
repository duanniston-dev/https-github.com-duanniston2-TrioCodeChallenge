package br.com.duannistontriocodechallenge.game.board.domain

import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardState
import br.com.duannistontriocodechallenge.game.board.data.Move
import br.com.duannistontriocodechallenge.game.board.data.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.random.Random

class GameBoardUseCase {


    val board: MutableStateFlow<Array<Array<GameBoardAdapterItemData>>> =
        MutableStateFlow(Array(7) { column ->
            Array(7) { row ->
                GameBoardAdapterItemData(
                    Position(column, row),
                    GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
                )
            }
        })


    private fun clearBoard(): Flow<GameBoardState> {
        return flow {
            val resetBoard = Array(7) { column ->
                Array(7) { row ->
                    GameBoardAdapterItemData(
                        Position(column, row),
                        GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
                    )
                }
            }
            board.value = resetBoard
            emit(GameBoardState.CLEANED)
        }
    }

    private fun putPrize(): Flow<GameBoardState> {

        return flow {
            val listFiltered = board.value.clone().flatMap { it.toList() }.filterNot { item ->
                ((item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT) ||
                        (item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT)
                        )

            }.toList().shuffled()


            val randomItem = listFiltered[Random.nextInt(listFiltered.size)]

            val newBoardValues = board.value.clone()

            newBoardValues[randomItem.position.x][randomItem.position.y].gameBoardAdapterType =
                GameBoardAdapterItemData.GameBoardAdapterType.PRIZE

            board.value = newBoardValues
            emit(GameBoardState.ADDED_PRIZE)
        }

    }

    private fun putRobots(): Flow<GameBoardState> {

        return flow {
            val newBoard = board.value.clone()

            newBoard[0][0] = GameBoardAdapterItemData(
                Position(0, 0),
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
            )

            newBoard[6][6] = GameBoardAdapterItemData(
                Position(6, 6),
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
            )
            board.value = newBoard
            emit(GameBoardState.ADDED_ROBOTS)
        }


    }

    fun startGame(): Flow<GameBoardState> {
        return flowOf(
            clearBoard(),
            putRobots(),
            putPrize(),
            flowOf(GameBoardState.READY_TO_START),
            moveRobot1()
        ).flattenConcat()
    }

    fun moveRobot1(): Flow<GameBoardState> {
        return flow {

            while (true) {
                emit(GameBoardState.RUNNING)
                val currentRobot1 = getCurrentRobot1()

                val movesRobot1 = Move.values().filter { move ->

                    val newPosition = currentRobot1.copy().position.move(move)
                    return@filter isPositionValid(newPosition) && robotCanFollowThisLine(
                        newPosition.x,
                        newPosition.y
                    )

                }

                if (movesRobot1.isNotEmpty()) {
                    val newBoard1 = board.value.clone()

                    newBoard1[currentRobot1.position.x][currentRobot1.position.y].gameBoardAdapterType =
                        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_LINE

                    val moveForRobot1 = movesRobot1.random()
                    val newRobot1Position = currentRobot1.position.move(moveForRobot1)
                    val newCurrentRobot1 = currentRobot1.copy(position = newRobot1Position)

                    if (newBoard1[newCurrentRobot1.position.x][newCurrentRobot1.position.y].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE) {
                        emit(GameBoardState.ROBOT_1_WIN)
                        break
                    }
                    newBoard1[newCurrentRobot1.position.x][newCurrentRobot1.position.y].gameBoardAdapterType =
                        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT

                    board.value = newBoard1
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
                    val newBoard2 = board.value.clone()

                    newBoard2[currentRobot2.position.x][currentRobot2.position.y].gameBoardAdapterType =
                        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_LINE

                    val moveForRobot2 = movesRobot2.random()
                    val newRobot2Position = currentRobot2.position.move(moveForRobot2)
                    val newCurrentRobot2 = currentRobot2.copy(position = newRobot2Position)

                    if (newBoard2[newCurrentRobot2.position.x][newCurrentRobot2.position.y].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE) {
                        emit(GameBoardState.ROBOT_2_WIN)
                        break
                    }
                    newBoard2[newCurrentRobot2.position.x][newCurrentRobot2.position.y].gameBoardAdapterType =
                        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT

                    board.value = newBoard2
                }
                if (movesRobot1.isEmpty() && movesRobot2.isEmpty()) {
                    emit(GameBoardState.GAME_FINISHED)
                    break
                }
                delay(500)
            }
        }
    }

    fun moveRobot2(): Flow<GameBoardState> {
        return flow {

            while (true) {

                val currentRobot2 = getCurrentRobot2()

                val movesRobot2 = Move.values().filter { move ->

                    val newPosition = currentRobot2.copy().position.move(move)
                    return@filter isPositionValid(newPosition) && robotCanFollowThisLine(
                        newPosition.x,
                        newPosition.y
                    )

                }

                val newBoard = board.value.clone()

                newBoard[currentRobot2.position.x][currentRobot2.position.y].gameBoardAdapterType =
                    GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_LINE


                val moveForRobot2 = movesRobot2.random()
                val newRobot1Position = currentRobot2.position.move(moveForRobot2)
                val newCurrentRobot2 = currentRobot2.copy(position = newRobot1Position)

                if (newBoard[newCurrentRobot2.position.y][newCurrentRobot2.position.y].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE) {
                    emit(GameBoardState.ROBOT_2_WIN)
                    break
                }
                newBoard[newCurrentRobot2.position.x][newCurrentRobot2.position.y].gameBoardAdapterType =
                    GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT

                delay(500)
                board.value = newBoard
                emit(GameBoardState.RUNNING)
            }
        }
    }

    private fun getCurrentRobot1(): GameBoardAdapterItemData {
        val currentRobot1 = board.value.clone().flatMap { it.toList() }.find {
            it.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
        }!!
        return currentRobot1
    }

    private fun getCurrentRobot2(): GameBoardAdapterItemData {
        val currentRobot2 = board.value.clone().flatMap { it.toList() }.find {
            it.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT
        }!!
        return currentRobot2
    }

    private fun isPositionValid(position: Position): Boolean {
        return position.x in 0..6 && position.y in 0..6
    }

    private fun robotCanFollowThisLine(column: Int, row: Int): Boolean {
        return (board.value[column][row].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.EMPTY)
                || (board.value[column][row].gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.PRIZE)
    }

}