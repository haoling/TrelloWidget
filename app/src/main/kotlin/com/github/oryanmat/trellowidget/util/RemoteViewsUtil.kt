package com.github.oryanmat.trellowidget.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews

object RemoteViewsUtil {
    val METHOD_SET_ALPHA = "setAlpha"
    val METHOD_SET_COLOR_FILTER = "setColorFilter"
    val METHOD_SET_BACKGROUND = "setBackgroundColor"
    internal val IMAGE_SCALE = 1.5

    fun setTextView(context: Context, views: RemoteViews,
                    @IdRes textView: Int, text: String,
                    @ColorInt color: Int, @DimenRes dimen: Int) {
        setTextView(views, textView, text, color)
        views.setTextViewTextSize(textView, TypedValue.COMPLEX_UNIT_SP,
                getScaledValue(context, dimen))
    }

    fun setTextView(views: RemoteViews, @IdRes textView: Int,
                    text: String, @ColorInt color: Int) {
        views.setTextViewText(textView, text)
        views.setTextColor(textView, color)
    }

    fun setImage(context: Context, views: RemoteViews,
                 @IdRes view: Int, @DrawableRes image: Int) {
        val drawable = ContextCompat.getDrawable(context, image)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val density = context.resources.displayMetrics.density
        val prefTextScale = context.getPrefTextScale()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                (bitmap.width.toDouble() * IMAGE_SCALE * prefTextScale.toDouble() / density).toInt(),
                (bitmap.height.toDouble() * IMAGE_SCALE * prefTextScale.toDouble() / density).toInt(), true)
        views.setImageViewBitmap(view, scaledBitmap)
    }

    fun setImageViewColor(views: RemoteViews, @IdRes view: Int, @ColorInt color: Int) {
        val opaqueColor = Color.rgb(red(color), green(color), blue(color))
        views.setInt(view, METHOD_SET_COLOR_FILTER, opaqueColor)
        views.setInt(view, METHOD_SET_ALPHA, alpha(color))
    }

    fun setBackgroundColor(views: RemoteViews, @IdRes view: Int, @ColorInt color: Int) {
        views.setInt(view, METHOD_SET_BACKGROUND, color)
    }

    fun getScaledValue(context: Context, @DimenRes dimen: Int): Float {
        val dimension = context.resources.getDimension(dimen)
        val density = context.resources.displayMetrics.density
        val prefTextScale = context.getPrefTextScale()
        return dimension * prefTextScale / density
    }

    fun hideView(views: RemoteViews, @IdRes view: Int) {
        views.setViewVisibility(view, View.GONE)
    }

    fun showView(views: RemoteViews, @IdRes view: Int) {
        views.setViewVisibility(view, View.VISIBLE)
    }

    fun optionallyHideView(views: RemoteViews, context: Context, @IdRes view: Int, @StringRes preferenceKey: Int) =
            views.setViewVisibility(view, (if (context.isEnabled(preferenceKey)) View.VISIBLE else View.GONE))

}
