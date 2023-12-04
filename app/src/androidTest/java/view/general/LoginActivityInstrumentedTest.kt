package view.general

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java)

    @Test
    fun onLoginAttempt_LoginFailsWithNoCredentials() {
        onView(withId(R.id.loginBtn)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun onLoginAttempt_LoginFailsWithBadEmailAddress() {
        onView(withId(R.id.Email)).perform(typeText("BadEmailAddress"))
        onView(withId(R.id.loginBtn)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun onLoginAttempt_LoginFailsWithPasswordButNoEmail() {
        onView(withId(R.id.password)).perform(typeText("HelloWorld"))
        onView(withId(R.id.loginBtn)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }
}
