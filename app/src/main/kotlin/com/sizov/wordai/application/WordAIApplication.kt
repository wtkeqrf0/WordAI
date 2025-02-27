package com.sizov.wordai.application

import android.app.Application

class WordAIApplication : Application() {
    val appComponent: AppComponent by lazy { AppComponent(applicationContext) }
}
