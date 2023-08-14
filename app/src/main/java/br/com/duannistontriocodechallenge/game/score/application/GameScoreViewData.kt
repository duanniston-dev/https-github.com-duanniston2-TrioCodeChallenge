package br.com.duannistontriocodechallenge.game.score.application

import br.com.duannistontriocodechallenge.game.score.data.repository.GameScoreDataResponse
import kotlinx.serialization.Serializable

@Serializable
data class GameScoreViewData(val robot1: String, val robot2: String, val noOneWin: String)

fun GameScoreDataResponse.toViewData(): GameScoreViewData {
    return GameScoreViewData(
        robot1 = this.robot1.toString(),
        robot2 = this.robot2.toString(),
        noOneWin = this.noOneWin.toString()
    )
}