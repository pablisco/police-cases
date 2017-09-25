package uk.crimeapp.test

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

class AppInjectingTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        val testApp: TestApp = super.newApplication(cl, TestApp::class.java.name, context) as TestApp
        testApp.original = super.newApplication(cl, className, context)
        return testApp
    }

}