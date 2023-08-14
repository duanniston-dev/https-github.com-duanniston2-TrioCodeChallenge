package br.com.duannistontriocodechallenge.game

import br.com.duannistontriocodechallenge.game.board.application.viewmodel.GameBoardViewModel
import br.com.duannistontriocodechallenge.game.board.domain.GameBoardUseCase
import br.com.duannistontriocodechallenge.game.score.application.viewmodel.GameScoreViewModel
import br.com.duannistontriocodechallenge.game.score.data.repository.GameScoreRepository
import br.com.duannistontriocodechallenge.game.score.domain.GameScoreUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object GameModule {

    val module = module {
        factory { GameBoardUseCase(gameScoreUseCase = get()) }
        viewModel {
            GameBoardViewModel(
                application = androidApplication(),
                gameBoardUseCase = get()
            )
        }
        viewModel {
            GameScoreViewModel(application = androidApplication(), gameScoreUseCase = get())
        }
        single {
            GameScoreRepository(context = androidContext())
        }
        factory {
            GameScoreUseCase(gameScoreRepository = get())
        }

    }
}