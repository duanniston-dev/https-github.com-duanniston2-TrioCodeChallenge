package br.com.duannistontriocodechallenge.game.board.application.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.domain.GameBoardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class GameBoardViewModel(application: Application, val gameBoardUseCase: GameBoardUseCase) :
    AndroidViewModel(application) {

    val boardLiveData: MutableLiveData<List<GameBoardAdapterItemData>> = MutableLiveData()

    init {
        gameBoardUseCase.startGame().onStart {

        }.flatMapConcat {
            gameBoardUseCase.board()
        }.onEach { items ->
            boardLiveData.postValue(items)
        }.catch {
            Log.e("GameBoardViewModel", "Error", it)
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}