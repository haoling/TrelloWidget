package com.github.oryanmat.trellowidget.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.github.oryanmat.trellowidget.TrelloWidget;
import com.github.oryanmat.trellowidget.activity.MoveCardActivity;
import com.github.oryanmat.trellowidget.model.Card;
import com.github.oryanmat.trellowidget.util.IntentUtil;

/**
 * A Service to dispatch intents from CardRemoteViewFactory because a RemoweViewFactory cannot fully dispatch different intents for multiple widgets in a single list view item
 *
 * Created by jramsay on 6/8/2016.
 */
public class CardListDispatcherService extends IntentService {
    public static final String ACTION_CARD_LIST = "com.github.oryanmat.trellowidget.CardListDispatcher";
    public static final String EXTRA_METHOD = "com.github.oryanmat.trellowidget.dispatchMethod";

    public CardListDispatcherService() {
        super("CardListDispatcherService");
    }

    public enum Method {
        VIEW,
        MOVE,
    }

    public static Intent generateIntent(Context context, Method method, int appWidgetId, Card card) // And board list?
    {
        Intent intent = new Intent(ACTION_CARD_LIST, Uri.EMPTY, context, CardListDispatcherService.class);
        intent.putExtra(EXTRA_METHOD, method.toString());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(TrelloWidgetProvider.EXTRA_CARD, card.toJson());
        return intent;
    }

    public static Intent generateViewIntent(Context context, int appWidgetId, Card card)
    {
        return generateIntent(context, Method.VIEW, appWidgetId, card);
    }


    public static Intent generateMoveActivityIntent(Context context, int appWidgetId, Card card, String prevPos, String nextPos) // And board list?
    {
        Intent intent = generateIntent(context, Method.MOVE,appWidgetId, card);
        intent.putExtra(MoveCardActivity.EXTRA_NEXTPOS, nextPos);
        intent.putExtra(MoveCardActivity.EXTRA_PREVPOS, prevPos);
        return intent;
    }

    public static PendingIntent generateIntentTemplate(Context context)
    {
        Intent intent = new Intent(ACTION_CARD_LIST, Uri.EMPTY, context, CardListDispatcherService.class);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Context context = this;
        Bundle extras = intent.getExtras();
        Card card = Card.parse(extras.getString(TrelloWidgetProvider.EXTRA_CARD));
        Method method = Method.valueOf(extras.getString(EXTRA_METHOD));

        Log.d(TrelloWidget.T_WIDGET, "Dispatching a " + extras.getString(EXTRA_METHOD) + " request for card " + card.toString());
        Intent nextIntent = null;
        switch(method) {
            case VIEW:
                nextIntent = IntentUtil.createViewCardIntent(card);
                break;
            case MOVE:
                nextIntent = MoveCardActivity.createMoveCardIntent(context, extras);
                break;
        }
        if (nextIntent != null) {
            nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(nextIntent);
        }
    }
}
