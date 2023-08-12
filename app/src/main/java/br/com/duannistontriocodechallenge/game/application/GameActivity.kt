package br.com.duannistontriocodechallenge.game.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.duannistontriocodechallenge.R

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
    }
}