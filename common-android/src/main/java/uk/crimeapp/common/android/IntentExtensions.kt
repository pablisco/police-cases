package uk.crimeapp.common.android

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import uk.crimeapp.common.di.Graph

fun Context.startActivity(f: Intent.() -> Unit) =
    Graph.from(this).navigationModule.startActivity(this, Intent().apply(f))

inline fun <reified T : Activity> Context.start(noinline f: Intent.() -> Unit = {}) =
    startActivity {
        f(this)
        component = ComponentName(this@start, T::class.java)
    }