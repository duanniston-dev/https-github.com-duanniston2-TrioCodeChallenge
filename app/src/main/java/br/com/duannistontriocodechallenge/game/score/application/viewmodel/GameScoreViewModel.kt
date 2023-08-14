package br.com.duannistontriocodechallenge.game.score.application.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.duannistontriocodechallenge.core.Resource
import br.com.duannistontriocodechallenge.core.postResourceGenericError
import br.com.duannistontriocodechallenge.game.score.application.GameScoreViewData
import br.com.duannistontriocodechallenge.game.score.application.toViewData
import br.com.duannistontriocodechallenge.game.score.domain.GameScoreUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class GameScoreViewModel(application: Application, val gameScoreUseCase: GameScoreUseCase) :
    AndroidViewModel(application) {

    val gameScoreLiveData: MutableLiveData<Resource<GameScoreViewData>> = MutableLiveData()

    init {
        gameScoreUseCase.load().flowOn(Dispatchers.IO).onStart {
            gameScoreLiveData.value = Resource.Loading
        }.onEach {
            when (it) {
                is Resource.Error -> {
                    gameScoreLiveData.value = it
                }

                Resource.Loading -> {

                }

                is Resource.Success -> {
                    gameScoreLiveData.value = Resource.Success(it.data.toViewData())
                }
            }
        }.catch {
            gameScoreLiveData.postResourceGenericError(it)
        }.flowOn(Dispatchers.Main).launchIn(viewModelScope)
    }
}