package com.github.oryanmat.trellowidget.util;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.github.oryanmat.trellowidget.activity.AddCardActivity;
import com.github.oryanmat.trellowidget.activity.ConfigActivity;
import com.github.oryanmat.trellowidget.model.Card;
import com.github.oryanmat.trellowidget.widget.TrelloWidgetProvider;

/**
 * Helper functions for creating various Trello Widget intents
 *
 * Created by jramsay on 5/18/2016.
 */
public class IntentUtil {

    static public Intent createRefreshIntent(Context context, int appWidgetId) {
        Intent refreshIntent = new Intent(context, TrelloWidgetProvider.class);
        refreshIntent.setAction(TrelloWidgetProvider.REFRESH_ACTION);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return refreshIntent;
    }

    static public Intent createReconfigureIntent(Context context, int appWidgetId)
    {
        Intent reconfigIntent = new Intent(context, ConfigActivity.class);
        reconfigIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        reconfigIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        reconfigIntent.putExtra(ConfigActivity.CONFIG_ACTIVITY_IS_RECONFIG, true);
        return reconfigIntent;
    }

    static public Intent createAddCardIntent(Context context, int appWidgetId)
    {
        Intent addIntent = new Intent(context, AddCardActivity.class);
        addIntent.setAction(TrelloWidgetProvider.ADD_ACTION);
        addIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return addIntent;
    }

    static public Intent createViewCardIntent(Card card)
    {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(card.url));
    }

}
