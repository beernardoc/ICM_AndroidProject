package br.com.androidproject

import android.app.Application
import br.com.androidproject.di.appModule
import br.com.androidproject.di.firebaseModule
import br.com.androidproject.di.storageModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AndroidProjectApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@AndroidProjectApplication)
            modules(
                appModule,
                storageModule,
                firebaseModule
            )
        }
    }
}