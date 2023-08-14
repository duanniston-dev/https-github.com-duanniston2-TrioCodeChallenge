package br.com.duannistontriocodechallenge.game.score.data.repository

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GameScoreDataResponse(val robot1: Int = 0, val robot2: Int = 0, val noOneWin: Int = 0)