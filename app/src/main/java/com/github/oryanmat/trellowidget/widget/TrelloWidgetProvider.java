package com.github.oryanmat.trellowidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.github.oryanmat.trellowidget.R;
import com.github.oryanmat.trellowidget.TrelloWidget;
import com.github.oryanmat.trellowidget.model.Board;
import com.github.oryanmat.trellowidget.model.BoardList;
import com.github.oryanmat.trellowidget.util.IntentUtil;
import com.github.oryanmat.trellowidget.util.PrefUtil;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS;
import static com.github.oryanmat.trellowidget.TrelloWidget.T_WIDGET;
import static com.github.oryanmat.trellowidget.util.PrefUtil.isTwoLineTitle;
import static com.github.oryanmat.trellowidget.util.RemoteViewsUtil.hideView;
import static com.github.oryanmat.trellowidget.util.RemoteViewsUtil.setBackground;
import static com.github.oryanmat.trellowidget.util.RemoteViewsUtil.setImageViewColor;
import static com.github.oryanmat.trellowidget.util.RemoteViewsUtil.setTextView;
import static com.github.oryanmat.trellowidget.util.RemoteViewsUtil.showView;
import static com.github.oryanmat.trellowidget.util.color.ColorUtil.dim;

public class TrelloWidgetProvider extends AppWidgetProvider {
    public static final String ADD_ACTION = "com.github.oryanmat.trellowidget.addAction";
    public static final String REFRESH_ACTION = "com.github.oryanmat.trellowidget.refreshAction";
    public static final String WIDGET_ID = "com.github.oryanmat.trellowidget.widgetId";
    public static final String TRELLO_PACKAGE_NAME = "com.trello";
    public static final String TRELLO_URL = "https://www.trello.com";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // TODO: We should update both the BoardList and Board on a refresh
        BoardList list = TrelloWidget.getList(context, appWidgetId);
        Board board = TrelloWidget.getBoard(context, appWidgetId);
        @ColorInt int cardFgColor = PrefUtil.getCardForegroundColor(context);
        @ColorInt int cardBgcolor = PrefUtil.getCardBackgroundColor(context);
        @ColorInt int titleFgColor = PrefUtil.getTitleForegroundColor(context);
        @ColorInt int titleBgColor = PrefUtil.getTitleBackgroundColor(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.trello_widget);

        // Set up the title bar
        if (isTwoLineTitle(context) && !board.id.equals("-1")) {
            showView(views, R.id.two_line_title);
            setTextView(views, R.id.board_title, board.name, titleFgColor);
            setTextView(views, R.id.list_subtitle, list.name, titleFgColor);
            hideView(views, R.id.list_title);
        } else {
            showView(views, R.id.list_title);
            setTextView(views, R.id.list_title, list.name, titleFgColor);
            hideView(views, R.id.two_line_title);
        }

        setImageViewColor(views, R.id.addButton, dim(titleFgColor));
        setImageViewColor(views, R.id.refreshButt, dim(titleFgColor));
        setImageViewColor(views, R.id.reconfigureButt, dim(titleFgColor));
        setBackground(views, R.id.title_bar, titleBgColor);
        views.setOnClickPendingIntent(R.id.addButton, getAddPendingIntent(context, appWidgetId));
        views.setOnClickPendingIntent(R.id.refreshButt, getRefreshPendingIntent(context, appWidgetId));
        views.setOnClickPendingIntent(R.id.reconfigureButt, getReconfigPendingIntent(context, appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_title, getTitleIntent(context, board));

        // Set up the card list
        setImageViewColor(views, R.id.divider, cardFgColor);
        views.setEmptyView(R.id.card_list, R.id.empty_card_list);
        views.setTextColor(R.id.empty_card_list, cardFgColor);
        setBackground(views, R.id.card_frame, cardBgcolor);
        views.setPendingIntentTemplate(R.id.card_list, getCardPendingIntent(context));
        views.setRemoteAdapter(R.id.card_list, getRemoteAdapterIntent(context, appWidgetId));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private Intent getRemoteAdapterIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        return intent;
    }

    private PendingIntent getAddPendingIntent(Context context, int appWidgetId) {
        Intent addIntent = IntentUtil.createAddCardIntent(context, appWidgetId);
        return PendingIntent.getActivity(context, appWidgetId, addIntent, 0);
    }

    private PendingIntent getRefreshPendingIntent(Context context, int appWidgetId) {
        Intent refreshIntent = IntentUtil.createRefreshIntent(context, appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, 0);
    }

    private PendingIntent getReconfigPendingIntent(Context context, int appWidgetId) {
        Intent reconfigIntent = IntentUtil.createReconfigureIntent(context, appWidgetId);
        return PendingIntent.getActivity(context, appWidgetId, reconfigIntent, 0);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(REFRESH_ACTION)) {
            Log.i(T_WIDGET, "Received refresh intent: Refreshing widget contents");
            notifyDataChanged(context, intent.getIntExtra(WIDGET_ID, 0));
        }
    }

    private PendingIntent getCardPendingIntent(Context context) {
        // individual card URIs are set in a RemoteViewsFactory.setOnClickFillInIntent
        return IntentUtil.createViewCardIntentTemplate(context);
    }

    private PendingIntent getTitleIntent(Context context, Board board) {
        Intent intent = PrefUtil.isTitleEnabled(context) ? getBoardIntent(context, board) : new Intent();
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private Intent getBoardIntent(Context context, Board board) {
        return !board.url.isEmpty() ?
                new Intent(Intent.ACTION_VIEW, Uri.parse(board.url)) :
                getTrelloIntent(context);
    }

    private Intent getTrelloIntent(Context context) {
        // try to find trello's app if installed. otherwise just open the website.
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(TRELLO_PACKAGE_NAME);
        return intent != null ? intent : new Intent(Intent.ACTION_VIEW, Uri.parse(TRELLO_URL));
    }

    public static void updateWidgetsData(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName compName = new ComponentName(context, TrelloWidgetProvider.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(compName);
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.card_list);
    }

    public static void updateWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName compName = new ComponentName(context, TrelloWidgetProvider.class);
        sendUpdateBroadcast(context, appWidgetManager.getAppWidgetIds(compName));
    }

    public static void updateWidget(Context context, int appWidgetId) {
        sendUpdateBroadcast(context, appWidgetId);
        notifyDataChanged(context, appWidgetId);
    }

    private static void sendUpdateBroadcast(Context context, int... appWidgetIds) {
        Intent intent = new Intent(context, TrelloWidgetProvider.class);
        intent.setAction(ACTION_APPWIDGET_UPDATE);
        intent.putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);
    }

    private static void notifyDataChanged(Context context, int... appWidgetIds) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.card_list);
    }
}
