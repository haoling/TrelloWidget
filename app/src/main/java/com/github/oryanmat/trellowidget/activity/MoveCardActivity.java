package com.github.oryanmat.trellowidget.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.oryanmat.trellowidget.R;
import com.github.oryanmat.trellowidget.TrelloWidget;
import com.github.oryanmat.trellowidget.model.Card;
import com.github.oryanmat.trellowidget.util.IntentUtil;
import com.github.oryanmat.trellowidget.util.Json;
import com.github.oryanmat.trellowidget.widget.TrelloWidgetProvider;

/**
 * This activity pops up a dialog to move a given card
 *
 * Created by jramsay on 6/7/2016.
 */
public class MoveCardActivity extends Activity {
    private Card card;
    private int appWidgetId;
    private boolean cardMoved = false;

    static public Intent createMoveCardIntent(Context context, Card card, int appWidgetId)
    {
        Intent moveCardIntent = new Intent(TrelloWidgetProvider.MOVE_CARD_ACTION, Uri.EMPTY, context, MoveCardActivity.class);
        moveCardIntent.putExtra(TrelloWidgetProvider.EXTRA_CARD, Json.get().toJson(card));
        moveCardIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return moveCardIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        card = Card.parse(extras.getString(TrelloWidgetProvider.EXTRA_CARD));
        appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        setContentView(R.layout.activity_move_card);
        setupTitle(card.name);
        setupButtons();
    }

    protected void setupTitle(String cardName)
    {
        TextView text = (TextView)findViewById(R.id.move_card_card_name);
        text.setText(cardName);
    }

    protected void setupButtons()
    {
        Button button = (Button)findViewById(R.id.move_card_top_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToTop();
            }
        });
        button = (Button)findViewById(R.id.move_card_up_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveUp();
            }
        });
        button = (Button)findViewById(R.id.move_card_list_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToList();
            }
        });
        button = (Button)findViewById(R.id.move_card_down_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDown();
            }
        });
        button = (Button)findViewById(R.id.move_card_bottom_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToBottom();
            }
        });
    }

    protected void moveToTop()
    {
        Log.d(TrelloWidget.T_WIDGET, "Would move card " + card.toString() + " to top");
        close();
    }

    protected void moveUp()
    {
        Log.d(TrelloWidget.T_WIDGET, "Would move card " + card.toString() + " up by one");
        close();
    }

    protected void moveDown()
    {
        Log.d(TrelloWidget.T_WIDGET, "Would move card " + card.toString() + " by one");
        close();
    }

    protected void moveToBottom()
    {
        Log.d(TrelloWidget.T_WIDGET, "Would move card " + card.toString() + " by one");
        close();
    }

    protected void moveToList()
    {
        Log.d(TrelloWidget.T_WIDGET, "Would move card " + card.toString() + " to ... some other list");
        close();
    }

    protected void close()
    {
        finish();
        if (cardMoved) {
            Intent refreshIntent = IntentUtil.createRefreshIntent(this, appWidgetId);
            sendBroadcast(refreshIntent);
        }
    }
}
