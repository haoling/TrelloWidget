package com.github.oryanmat.trellowidget.util;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.github.oryanmat.trellowidget.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class PrefUtil {

    public static float getPrefTextScale(Context context) {
        return Float.parseFloat(getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_text_size_key),
                context.getString(R.string.pref_text_size_default)));
    }

    public static int getInterval(Context context) {
        return Integer.parseInt(getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_update_interval_key),
                        context.getString(R.string.pref_update_interval_default)));
    }

    public static @ColorInt int getCardBackgroundColor(Context context) {
        return getColor(context, context.getString(R.string.pref_back_color_key),
                context.getResources().getInteger(R.integer.pref_back_color_default));
    }

    public static @ColorInt int getCardForegroundColor(Context context) {
        return getColor(context, context.getString(R.string.pref_fore_color_key),
                context.getResources().getInteger(R.integer.pref_fore_color_default));
    }
    public static @ColorInt int getTitleBackgroundColor(Context context) {
        if (isTitleUniqueColor(context)) {
            return getColor(context, context.getString(R.string.pref_title_back_color_key),
                    context.getResources().getInteger(R.integer.pref_title_back_color_default));
        }
        return getCardBackgroundColor(context);
    }

    public static @ColorInt int getTitleForegroundColor(Context context) {
        if (isTitleUniqueColor(context)) {
            return getColor(context, context.getString(R.string.pref_title_fore_color_key),
                    context.getResources().getInteger(R.integer.pref_title_fore_color_default));
        }
        return getCardForegroundColor(context);
    }

    public static @ColorInt int getColor(Context context, String key, int defValue) {
        return getDefaultSharedPreferences(context).getInt(key, defValue);
    }

    public static boolean isTitleUniqueColor(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.pref_title_use_unique_color_key),
                context.getResources().getBoolean(R.bool.pref_title_use_unique_color_default));
    }

    public static boolean isTitleEnabled(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.pref_title_onclick_key),
                context.getResources().getBoolean(R.bool.pref_title_onclick_default));
    }
}
