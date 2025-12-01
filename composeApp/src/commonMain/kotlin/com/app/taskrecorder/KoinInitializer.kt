package com.app.taskrecorder

import com.app.taskrecorder.di.appModule
import com.app.taskrecorder.di.platformModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, appModule)
    }
}
