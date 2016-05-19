package com.github.oryanmat.trellowidget.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.oryanmat.trellowidget.R;
import com.github.oryanmat.trellowidget.TrelloWidget;
import com.github.oryanmat.trellowidget.model.Board;
import com.github.oryanmat.trellowidget.model.BoardList;
import com.github.oryanmat.trellowidget.model.NewCard;
import com.github.oryanmat.trellowidget.util.IntentUtil;
import com.github.oryanmat.trellowidget.util.TrelloAPIUtil;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static com.github.oryanmat.trellowidget.TrelloWidget.T_WIDGET;

/**
 * Activity to add one or more cards to the current widget's list
 *
 * Created by jramsay on 5/17/2016.
 */
public class AddCardActivity extends Activity {
    int appWidgetId = INVALID_APPWIDGET_ID;
    int cardsAdded = 0;

    enum Location {
        INSERT_AT_TOP,
        INSERT_AT_BOTTOM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Bundle extras = getIntent().getExtras();
        cardsAdded = 0;
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
        setButtonsEnabled(true);
    }

    public void addNewCard(View view, Location where)
    {
        TextView title = (TextView)findViewById(R.id.add_card_title);
        String newTitle = title.getText().toString();
        if (newTitle.isEmpty()) {
            return;
        }
        setButtonsEnabled(false);

        BoardList list = TrelloWidget.getList(this, appWidgetId);
        Board board = TrelloWidget.getBoard(this, appWidgetId);
        NewCard newCard = new NewCard(list.id, newTitle);
        if (where == Location.INSERT_AT_TOP) {
            newCard.atTop();
        } else {
            newCard.atBottom();
        }
        Switch addMultiples = (Switch)findViewById(R.id.addMultiples);

        String listDescription = board.name + "/" + list.name;
        Log.d(T_WIDGET, "Adding new card to " + (where == Location.INSERT_AT_TOP ? "top" : "bottom") + " of " + listDescription + ": " + newTitle);
        AddCardListener listener = new AddCardListener(view, listDescription, addMultiples.isChecked());
        TrelloAPIUtil.instance.addNewCard(newCard, listener);

        // TODO: Start a spinner or something?
    }

    class AddCardListener implements Response.Listener<String>, Response.ErrorListener {
        View view;
        String description;
        boolean closeOnSuccess;
        public AddCardListener(View v, String listDescription, boolean addMultiples) {
            view = v;
            description = listDescription;
            closeOnSuccess = !addMultiples;
        }

        @Override
        public void onResponse(String response) {
            // TODO: The 'response' is the json of the newly-created card - We could maybe inject this into the RemoteView without forcing a refresh?
            Log.i(T_WIDGET, "Added card to " + description);
            cardsAdded++;
            if (closeOnSuccess) {
                close(view);
            } else {
                resetInput(view);
            }
            Toast.makeText(AddCardActivity.this, getString(R.string.add_card_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(T_WIDGET, "Add Card failed: " + error.networkResponse.data.toString(), error);
            String message = getString(R.string.add_card_failure);
            if (error.networkResponse.statusCode == 401) {
                // TODO: Maybe actually open the login dialog?
                message = getString(R.string.add_card_permission_failure);
            }
            Toast.makeText(AddCardActivity.this, message, Toast.LENGTH_LONG).show();
            setButtonsEnabled(true);
        }
    }

    public void resetInput(View view) {
        TextView title = (TextView)findViewById(R.id.add_card_title);
        title.setText("");
        setButtonsEnabled(true);
    }

    public void setButtonsEnabled(boolean enabled)
    {
        ImageButton close = (ImageButton)findViewById(R.id.addCloseButton);
        close.setEnabled(enabled);
        Button top = (Button)findViewById(R.id.topButton);
        top.setEnabled(enabled);
        Button bottom = (Button)findViewById(R.id.bottomButton);
        bottom.setEnabled(enabled);
    }

    public void close(View view) {
        finish();
        if (cardsAdded > 0) {
            Intent refreshIntent = IntentUtil.createRefreshIntent(this, appWidgetId);
            sendBroadcast(refreshIntent);
        }
    }
}
