package br.com.duannistontriocodechallenge.game.board.domain

import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.data.Position
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlin.random.Random

class GameBoardUseCase {

    private var currentRobot1 = GameBoardAdapterItemData(
        Position(0, 0),
        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
    )
    private var currentRobot2 = GameBoardAdapterItemData(
        Position(6, 6),
        GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
    )

    private val board: Array<Array<GameBoardAdapterItemData>> =
        Array(7) { column ->
            Array(7) { row ->
                GameBoardAdapterItemData(
                    Position(column, row),
                    GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
                )
            }
        }

    fun board(): Flow<List<GameBoardAdapterItemData>> {
        return flow {
            val items = mutableListOf<GameBoardAdapterItemData>()
            board.forEach { column ->
                column.forEach { row ->
                    items.add(row)
                }
            }
            emit(items)
        }
    }

    private fun clearBoard(): Flow<Unit> {
        return flow {
            board.forEach {
                it.forEach { item ->
                    item.gameBoardAdapterType =
                        GameBoardAdapterItemData.GameBoardAdapterType.EMPTY
                }
            }
            emit(Unit)
        }

    }

    private fun putPrize(): Flow<Unit> {

        return flow {
            val listFiltered = board().flatMapConcat {
                it.asFlow()
            }.filterNot { item ->
                ((item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT) ||
                        (item.gameBoardAdapterType == GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT)
                        )
            }.toList().shuffled()

            val randomItem = listFiltered[Random.nextInt(listFiltered.size)]

            board[randomItem.position.x][randomItem.position.y].gameBoardAdapterType =
                GameBoardAdapterItemData.GameBoardAdapterType.PRIZE

            emit(Unit)
        }


    }

    private fun putRobots(): Flow<Unit> {

        return flow {
            board[0][0] = GameBoardAdapterItemData(
                Position(0, 0),
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
            )
            board[6][6] = GameBoardAdapterItemData(
                Position(6, 6),
                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT
            )
            emit(Unit)
        }


    }

    fun startGame(): Flow<Unit> {
        return flowOf(clearBoard(), putRobots(), putPrize()).flattenConcat()
    }
}