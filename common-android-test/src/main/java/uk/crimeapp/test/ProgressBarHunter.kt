package uk.crimeapp.test

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.test.espresso.util.TreeIterables
import android.support.v4.content.ContextCompat
import android.widget.ProgressBar

/**
 * This lifecycle listener will search the layout of an activity that has been just started for instances of
 * [ProgressBar]. If found, it sets a decoy drawable to make it stop bothering Espresso.
 */
class ProgressBarHunter: Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(p0: Activity?) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityDestroyed(p0: Activity?) = Unit
    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) = Unit
    override fun onActivityStopped(p0: Activity?) = Unit
    override fun onActivityCreated(activity: Activity, p1: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        hideProgressBarAnimation(activity)
    }

    private fun hideProgressBarAnimation(activity: Activity) {
        TreeIterables.breadthFirstViewTraversal(activity.window.decorView)
            .filterIsInstance<ProgressBar>()
            .forEach { removeAnimation(it) }

    }

    private fun removeAnimation(progressBar: ProgressBar) {
        val nullDrawable = ContextCompat.getDrawable(progressBar.context, android.R.color.black)
        progressBar.indeterminateDrawable = nullDrawable
    }

}