package br.com.duannistontriocodechallenge.game

import br.com.duannistontriocodechallenge.game.board.application.viewmodel.GameBoardViewModel
import br.com.duannistontriocodechallenge.game.board.domain.GameBoardUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object GameModule {

    val module = module {
        factory { GameBoardUseCase() }
        viewModel {
            GameBoardViewModel(
                application = androidApplication(),
                gameBoardUseCase = get()
            )
        }
    }
}