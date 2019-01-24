package com.jpp.moviespreview.screens.main.search


import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.jpp.moviespreview.MPApp
import com.jpp.moviespreview.di.createFakeActivityInjector
import com.jpp.moviespreview.screens.main.MainActivity
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {


    //TODO JPP do I need this?
    @get:Rule
    var testRule = CountingTaskExecutorRule()

    @get:Rule
    val activityTestRule = object : ActivityTestRule<MainActivity>(MainActivity::class.java, true, true) {
        override fun beforeActivityLaunched() {
            val myApp = InstrumentationRegistry.getInstrumentation().targetContext as MPApp
            myApp.activityInjector = createFakeActivityInjector<MainActivity> {

            }
        }
    }

    /*
     * Using https://github.com/SabagRonen/dagger-activity-test-sample/blob/ActivitySample/app/src/androidTest/java/com/example/ronensabag/daggeractivitytestsample/MainViewTests.kt
     * (from https://proandroiddev.com/activity-espresso-test-with-daggers-android-injector-82f3ee564aa4)
     * 1 - Inject a mock ViewModelProvider.Factory.
     * 2 - The mock will provide an instance of each ViewModel (not a mock).
     * 3 - Each ViewModel will have a mock of the Repository.
     * 4 - Before executing a test, we need to navigate to the given fragment and wait for that
     *     fragment to be visible.
     */


}