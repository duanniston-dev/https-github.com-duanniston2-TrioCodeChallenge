package br.com.duannistontriocodechallenge.game.board.application.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardScoreData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardStateData
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
    val scoreRobotLiveData: MutableLiveData<GameBoardScoreData> = MutableLiveData()

    init {
        startObserverBoard()
        startObserverScore()
        startObserverGameState()
        startGame()
    }

    private fun startObserverBoard() {
        gameBoardUseCase.boardFlow.onStart {

        }.flowOn(Dispatchers.IO).onEach { board ->
            boardLiveData.value = board.flatMap { it.toList() }
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }
    private fun startObserverScore() {
        gameBoardUseCase.gameScoreFlow.onEach {
            scoreRobotLiveData.value = it
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }

    private fun startObserverGameState() {

        gameBoardUseCase.gameFlow.onEach {
            when (it) {
                GameBoardStateData.WAITING -> {

                }

                GameBoardStateData.CLEANED -> {

                }

                GameBoardStateData.ADDED_ROBOTS -> {

                }

                GameBoardStateData.ADDED_PRIZE -> {

                }

                GameBoardStateData.RUNNING -> {

                }

                GameBoardStateData.GAME_FINISHED -> {

                }
            }
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }

    private fun startGame() {
        gameBoardUseCase.startGame().flowOn(Dispatchers.IO).catch {
            Log.e("GameBoardViewModel", "Error", it)
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }
}