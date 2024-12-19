package com.example.work6

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.work6.presentation.view.MainActivity
import org.junit.Rule
import org.junit.Test

class MainActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testButtonIsDisplayed() {
        // Проверка, что кнопка отображается на экране
        onView(withId(R.id.cat_btn))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testButtonIsClickable() {
        // Проверка, что кнопка кликабельна
        onView(withId(R.id.cat_btn))
            .check(matches(isClickable()))
    }

    @Test
    fun testImageViewDisplayedAfterButtonClick() {
        // Нажатие на кнопку и проверка, что ImageView отображается
        onView(withId(R.id.cat_btn)).perform(click())
        onView(withId(R.id.cat_image_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testButtonTextIsCorrect() {
        // Проверка, что текст на кнопке соответствует ожидаемому
        onView(withId(R.id.cat_btn))
            .check(matches(withText("Fetch Cat")))
    }

    @Test
    fun testButtonRemainsDisplayedAfterClick() {
        // Нажатие на кнопку и проверка, что кнопка остается отображаемой после нажатия
        onView(withId(R.id.cat_btn)).perform(click())
        onView(withId(R.id.cat_btn))
            .check(matches(isDisplayed()))
    }

}
