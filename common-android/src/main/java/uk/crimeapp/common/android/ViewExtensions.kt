package uk.crimeapp.common.android

import android.view.View

inline fun List<View>.visibleWhen(
    invisibleAs: Int = android.view.View.GONE,
    requiredVisible: (View) -> Boolean
) {
    forEach { it.visibleWhen(invisibleAs, requiredVisible) }
}

inline fun View.visibleWhen(
    invisibleAs: Int = android.view.View.GONE,
    requiredVisible: (View) -> Boolean
) {
    visibility = if (requiredVisible(this)) View.VISIBLE else invisibleAs
}