package com.github.oryanmat.trellowidget.util

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import com.github.oryanmat.trellowidget.R

internal fun Context.getPrefTextScale() =
        java.lang.Float.parseFloat(sharedPreferences().getString(
                getString(R.string.pref_text_size_key),
                getString(R.string.pref_text_size_default)))

internal fun Context.getInterval() =
        Integer.parseInt(sharedPreferences().getString(
                getString(R.string.pref_update_interval_key),
                getString(R.string.pref_update_interval_default)))

internal @ColorInt fun Context.getCardBackgroundColor() = getColor(
        getString(R.string.pref_back_color_key),
        resources.getInteger(R.integer.pref_back_color_default))

internal @ColorInt fun Context.getCardForegroundColor() = getColor(
        getString(R.string.pref_fore_color_key),
        resources.getInteger(R.integer.pref_fore_color_default))

internal fun Context.isNoTitle() =
        isEnabled(R.string.pref_no_title_key)

internal fun Context.isTwoLineTitle() =
        isEnabled(R.string.pref_two_line_title_key)

internal fun Context.isTitleUniqueColor() =
        isEnabled(R.string.pref_title_use_unique_color_key)

internal fun Context.isTitleEnabled() =
        isEnabled(R.string.pref_title_onclick_key)

internal fun Context.isEnabled(@StringRes key: Int) =
        sharedPreferences().getBoolean(getString(key), true)

internal @ColorInt fun Context.getTitleBackgroundColor(): Int = when {
    isTitleUniqueColor() -> getColor(
            getString(R.string.pref_title_back_color_key),
            resources.getInteger(R.integer.pref_title_back_color_default))
    else -> getCardBackgroundColor()
}

internal @ColorInt fun Context.getTitleForegroundColor(): Int = when {
    isTitleUniqueColor() -> getColor(
            getString(R.string.pref_title_fore_color_key),
            resources.getInteger(R.integer.pref_title_fore_color_default))
    else -> getCardForegroundColor()
}

private @ColorInt fun Context.getColor(key: String, defValue: Int) =
        sharedPreferences().getInt(key, defValue)

private fun Context.sharedPreferences() = getDefaultSharedPreferences(this)
