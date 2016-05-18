package com.github.oryanmat.trellowidget.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.oryanmat.trellowidget.R;
import com.github.oryanmat.trellowidget.TrelloWidget;
import com.github.oryanmat.trellowidget.model.Board;
import com.github.oryanmat.trellowidget.model.BoardList;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static com.github.oryanmat.trellowidget.TrelloWidget.T_WIDGET;

/**
 * Created by jramsay on 5/17/2016.
 */
public class AddCardActivity extends Activity {
    int appWidgetId = INVALID_APPWIDGET_ID;
    enum Location {
        INSERT_AT_TOP,
        INSERT_AT_BOTTOM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == INVALID_APPWIDGET_ID) {
            Log.e(T_WIDGET, "Invalid widget ID: Cannot add a new card");
            finish();
            return;
        }

        BoardList list = TrelloWidget.getList(this, appWidgetId);
        Board board = TrelloWidget.getBoard(this, appWidgetId);

        TextView title = (TextView)findViewById(R.id.add_card_board_name);
        title.setText(String.format("%s / %s", board.name, list.name));

        ImageButton close = (ImageButton)findViewById(R.id.addCloseButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(v);
            }
        });
        Button top = (Button)findViewById(R.id.topButton);
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard(v, Location.INSERT_AT_TOP);
            }
        });
        Button bottom = (Button)findViewById(R.id.bottomButton);
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard(v, Location.INSERT_AT_BOTTOM);
            }
        });
    }

    public void addNewCard(View view, Location where)
    {
        TextView title = (TextView)findViewById(R.id.add_card_title);
        String newTitle = title.getText().toString();
        if (newTitle.isEmpty()) {
            return;
        }
        title.setText("");

        BoardList list = TrelloWidget.getList(this, appWidgetId);
        Board board = TrelloWidget.getBoard(this, appWidgetId);
        // TODO: Actually do the add

        Log.i(T_WIDGET, "Would add new card to " + (where == Location.INSERT_AT_TOP ? "top" : "bottom") + " of " + board.name + "/" + list.name + ": " + newTitle);

        // TODO: Optionally finish(); if the user only wants to add one card
    }

    public void close(View view) {
        finish(); // TODO: Send a widget refresh broadcast
    }
}
