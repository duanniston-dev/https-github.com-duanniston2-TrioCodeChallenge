package br.com.duannistontriocodechallenge.game.board.application.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.duannistontriocodechallenge.core.Resource
import br.com.duannistontriocodechallenge.core.postResourceGenericError
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
import timber.log.Timber

class GameBoardViewModel(application: Application, val gameBoardUseCase: GameBoardUseCase) :
    AndroidViewModel(application) {

    val boardLiveData: MutableLiveData<Resource<List<GameBoardAdapterItemData>>> = MutableLiveData()
    val scoreRobotLiveData: MutableLiveData<Resource<GameBoardScoreData>> = MutableLiveData()
    val gameFlowLiveData: MutableLiveData<Resource<GameBoardStateData>> = MutableLiveData()

    init {
        startObserverBoard()
        startObserverScore()
        startObserverGameState()
        startGame()
    }

    private fun startObserverBoard() {
        gameBoardUseCase.boardFlow.onStart {
            boardLiveData.postValue(Resource.Loading)
        }.flowOn(Dispatchers.IO).onEach { board ->
            boardLiveData.value = Resource.Success(board.flatMap { it.toList() })
        }.catch {
            boardLiveData.postResourceGenericError(it)
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }

    private fun startObserverScore() {
        gameBoardUseCase.gameScoreFlow.onStart {
            scoreRobotLiveData.postValue(Resource.Loading)
        }.onEach {
            scoreRobotLiveData.value = Resource.Success(it)
        }.catch {
            scoreRobotLiveData.postResourceGenericError(it)
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }

    private fun startObserverGameState() {

        gameBoardUseCase.gameFlow.onStart {
            gameFlowLiveData.postValue(Resource.Loading)
        }.onEach {
            gameFlowLiveData.value = Resource.Success(it)
        }.catch {
            gameFlowLiveData.postResourceGenericError(it)
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }

    private fun startGame() {
        gameBoardUseCase.startGame().catch {
            Timber.e(it)
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}