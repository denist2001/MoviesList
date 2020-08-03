package com.codechallenge.neugelb

import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.codechallenge.neugelb.di.MainModule
import com.codechallenge.neugelb.ui.main.MainAdapter
import com.codechallenge.neugelb.utils.getStringFrom
import com.codechallenge.neugelb.utils.recyclerItemAtPosition
import com.codechallenge.neugelb.utils.waitForOrientation
import com.codechallenge.neugelb.utils.waitUntilView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@UninstallModules(MainModule::class)
@HiltAndroidTest
class MainActivityNowPlayingTest {

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
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(getStringFrom("test_list_movies.json"))
            }
        }
        activityTestRule.launchActivity(null)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationNatural()
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).unfreezeRotation()
    }

    @Test
    fun checkIfAfterStartOneRequestGoestoServer() {
        //http://localhost:8080/movie/now_playing?page=1&api_key=f59338cd40961fb3fba86095332969e1
        waitUntilView(R.id.allMovies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        val requestBody = mockServer.takeRequest()
        assertTrue(requestBody.path!!.contains("page=1"))
        assertTrue(requestBody.path!!.contains("api_key=f59338cd40961fb3fba86095332969e1"))
    }

    @Test
    fun checkIfAfterScrollingToSizeMinus10Position_shouldMakeNewRequestGoToServer() {
        //http://localhost:8080/movie/now_playing?page=1&api_key=f59338cd40961fb3fba86095332969e1
        waitUntilView(R.id.allMovies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        //Scroll to the 10th index in json should request new movies
        onView(withId(R.id.allMovies_rv)).perform(
            RecyclerViewActions.scrollToPosition<MainAdapter.MainViewHolder>(10)
        )
        onView(withId(R.id.allMovies_rv))
            .check(
                matches(
                    recyclerItemAtPosition(
                        10,
                        hasDescendant(withText("The Hunt"))
                    )
                )
            )
        assertEquals(2, mockServer.requestCount)
        var requestBody = mockServer.takeRequest()
        assertTrue(requestBody.path!!.contains("page=1"))
        assertTrue(requestBody.path!!.contains("api_key=f59338cd40961fb3fba86095332969e1"))
        requestBody = mockServer.takeRequest()
        assertTrue(requestBody.path!!.contains("page=2"))
        assertTrue(requestBody.path!!.contains("api_key=f59338cd40961fb3fba86095332969e1"))
    }

    @Test
    fun checkIfClickOnItem_shouldOpenFragmentWithDetails() {
        //http://localhost:8080/movie/now_playing?page=1&api_key=f59338cd40961fb3fba86095332969e1
        waitUntilView(R.id.allMovies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        onView(withText("Inception")).check(matches(isDisplayed()))
        onView(withText("Inception")).perform(click())
        onView(withId(R.id.details)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfScreenRotates_shouldStayOnTheSamePosition() {
        //http://localhost:8080/movie/now_playing?page=1&api_key=f59338cd40961fb3fba86095332969e1
        waitUntilView(R.id.allMovies_rv, 3, isDisplayed())
        onView(withId(R.id.allMovies_rv)).perform(
            RecyclerViewActions.scrollToPosition<MainAdapter.MainViewHolder>(7)
        )
        onView(withId(R.id.allMovies_rv))
            .check(
                matches(
                    recyclerItemAtPosition(
                        7,
                        hasDescendant(withText("The Outpost"))
                    )
                )
            )
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationLeft()
        waitForOrientation(activityTestRule.activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        onView(withId(R.id.allMovies_rv))
            .check(
                matches(
                    recyclerItemAtPosition(
                        5,
                        hasDescendant(withText("Sonic the Hedgehog"))
                    )
                )
            )
    }

    @After
    fun tearDown() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationNatural()
        mockServer.shutdown()
    }
}