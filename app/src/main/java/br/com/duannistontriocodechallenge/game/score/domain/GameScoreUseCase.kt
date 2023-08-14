package br.com.duannistontriocodechallenge.game.score.domain

import br.com.duannistontriocodechallenge.core.Resource
import br.com.duannistontriocodechallenge.game.score.data.repository.GameScoreDataResponse
import br.com.duannistontriocodechallenge.game.score.data.repository.GameScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GameScoreUseCase(val gameScoreRepository: GameScoreRepository) {

    fun load(): Flow<Resource<GameScoreDataResponse>> {
        return gameScoreRepository.load().map {
            Resource.Success(it)
        }
    }

    suspend fun setScoreFromLastGame(robot1: Int, robot2: Int, noOneWin: Int): Flow<Resource<Unit>> {
        return flow {
            gameScoreRepository.updateScore(robot1, robot2, noOneWin)
            emit(Resource.Success(Unit))
        }
    }
}