package br.com.duannistontriocodechallenge.game.board.application.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.com.duannistontriocodechallenge.game.board.domain.GameBoardUseCase

class GameBoardViewModel(application: Application, val gameBoardUseCase: GameBoardUseCase) :
    AndroidViewModel(application) {


}