package br.com.duannistontriocodechallenge.core

import android.app.Application
import br.com.duannistontriocodechallenge.game.GameModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpKoin()
        setUpTimber()
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger()
            androidContext(this@AppApplication)
            modules(GameModule.module)
        }
    }

    private fun setUpTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }
}