package br.com.duannistontriocodechallenge.game.board.application.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.duannistontriocodechallenge.game.board.data.BoardGameScore
import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardState
import br.com.duannistontriocodechallenge.game.board.domain.GameBoardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class GameBoardViewModel(application: Application, val gameBoardUseCase: GameBoardUseCase) :
    AndroidViewModel(application) {

    val boardLiveData: MutableLiveData<List<GameBoardAdapterItemData>> = MutableLiveData()
    val scoreRobotLiveData: MutableLiveData<BoardGameScore> = MutableLiveData()

    init {
        gameBoardUseCase.board.onStart {

        }.flowOn(Dispatchers.IO).onEach { board ->
            boardLiveData.value = board.flatMap { it.toList() }
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)

        gameBoardUseCase.startGame().flowOn(Dispatchers.IO).onEach {
            when (it) {
                GameBoardState.CLEANED -> {

                }

                GameBoardState.ADDED_ROBOTS -> {

                }

                GameBoardState.ADDED_PRIZE -> {

                }

                GameBoardState.READY_TO_START -> {

                }

                GameBoardState.ROBOT_1_WIN,
                GameBoardState.ROBOT_2_WIN -> {
                    updateScoreRobot(it)
                }

                GameBoardState.RUNNING -> {

                }

                GameBoardState.GAME_FINISHED -> {

                }
            }
        }.catch {
            Log.e("GameBoardViewModel", "Error", it)
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)


    }

    private fun updateScoreRobot(gameBoardState: GameBoardState) {
        if (scoreRobotLiveData.isInitialized) {
            when (gameBoardState) {
                GameBoardState.ROBOT_1_WIN -> {
                    scoreRobotLiveData.value =
                        scoreRobotLiveData.value!!.copy(robot1 = scoreRobotLiveData.value!!.robot1 + 1)
                }

                GameBoardState.ROBOT_2_WIN -> {
                    scoreRobotLiveData.value =
                        scoreRobotLiveData.value!!.copy(robot2 = scoreRobotLiveData.value!!.robot2 + 1)
                }

                else -> {

                }
            }
        } else {
            when (gameBoardState) {
                GameBoardState.ROBOT_1_WIN -> {
                    scoreRobotLiveData.value = BoardGameScore(1,0)
                }

                GameBoardState.ROBOT_2_WIN -> {
                    scoreRobotLiveData.value =BoardGameScore(0,1)
                }

                else -> {

                }
            }
        }
    }
}