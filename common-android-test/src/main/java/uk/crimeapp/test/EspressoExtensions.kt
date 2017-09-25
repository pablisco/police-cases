package uk.crimeapp.test

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

inline fun <reified T : Activity> activityRule(
    noinline afterLaunch: () -> Unit = {},
    noinline afterFinish: () -> Unit = {},
    noinline beforeLaunch: () -> Unit = {}
) = object : ActivityTestRule<T>(T::class.java) {

    override fun afterActivityFinished() = afterFinish()

    override fun afterActivityLaunched() = afterLaunch()

    override fun beforeActivityLaunched() = beforeLaunch()

}

fun <T> match(describe: (Description?) -> Unit = {}, f: (T) -> Boolean) = object : TypeSafeMatcher<T>() {

    override fun matchesSafely(item: T): Boolean = f(item)

    override fun describeTo(description: Description?) = describe(description)

}