package br.com.duannistontriocodechallenge.game.score.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.serialization.json.Json

private const val GAME_SCORE_KEY = "GAME_SCORE_KEY"

class GameScoreRepository(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_score")
    private val gameScoreData = stringPreferencesKey(GAME_SCORE_KEY)
    fun load(): Flow<GameScoreDataResponse> {
        return context.dataStore.data.map {
            return@map it.toObject()
        }.take(1)
    }

    private fun Preferences.toObject(): GameScoreDataResponse {
        val currentRawData = this[gameScoreData] ?: "{}"
        return Json.decodeFromString(GameScoreDataResponse.serializer(), currentRawData)
    }

    suspend fun updateScore(robot1: Int, robot2: Int, noOneWin: Int) {
        context.dataStore.edit { settings ->
            val currentObj = settings.toObject()

            val updatedObj = currentObj.copy(
                robot1 = currentObj.robot1 + robot1,
                robot2 = currentObj.robot2 + robot2,
                noOneWin = currentObj.noOneWin + noOneWin
            )

            settings[gameScoreData] =
                Json.encodeToString(GameScoreDataResponse.serializer(), updatedObj)
        }
    }

}