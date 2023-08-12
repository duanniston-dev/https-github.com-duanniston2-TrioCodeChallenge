package br.com.duannistontriocodechallenge.core

import android.app.Application
import br.com.duannistontriocodechallenge.game.GameModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpKoin()
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger()
            androidContext(this@AppApplication)
            modules(GameModule.module)
        }
    }
}