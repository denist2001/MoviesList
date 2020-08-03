package com.codechallenge.neugelb

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.codechallenge.neugelb.di.MainModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@UninstallModules(MainModule::class)
@HiltAndroidTest
class MalformedResponseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        hiltRule.inject()
        mockServer = MockWebServer()
        mockServer.start(8080)
    }

    @Test
    fun checkIf401Error_shouldShowToast() {
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(401)
                    .setBody("{\n" +
                            "  \"status_message\": \"Invalid API key: You must be granted a valid key.\",\n" +
                            "  \"success\": false,\n" +
                            "  \"status_code\": 7\n" +
                            "}")
            }
        }
        activityTestRule.launchActivity(null)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationNatural()
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).unfreezeRotation()
        //http://localhost:8080/movie/now_playing?page=1&api_key=f59338cd40961fb3fba86095332969e1
        Espresso.onView(ViewMatchers.withText("Server side error"))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.not(
                        Matchers.`is`(
                            activityTestRule.activity.window.decorView
                        )
                    )
                )
            )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkIf404Error_shouldShowToast() {
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(404)
                    .setBody("{\n" +
                            "  \"status_message\": \"The resource you requested could not be found.\",\n" +
                            "  \"status_code\": 34\n" +
                            "}")
            }
        }
        activityTestRule.launchActivity(null)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationNatural()
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).unfreezeRotation()
        //http://localhost:8080/movie/now_playing?page=1&api_key=f59338cd40961fb3fba86095332969e1
        Espresso.onView(ViewMatchers.withText("Server side error"))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.not(
                        Matchers.`is`(
                            activityTestRule.activity.window.decorView
                        )
                    )
                )
            )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @After
    fun tearDown() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationNatural()
        mockServer.shutdown()
    }
}