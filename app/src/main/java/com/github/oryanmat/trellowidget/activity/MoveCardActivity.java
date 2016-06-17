package com.github.oryanmat.trellowidget.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.oryanmat.trellowidget.R;
import com.github.oryanmat.trellowidget.TrelloWidget;
import com.github.oryanmat.trellowidget.model.Board;
import com.github.oryanmat.trellowidget.model.BoardList;
import com.github.oryanmat.trellowidget.model.Card;
import com.github.oryanmat.trellowidget.util.IntentUtil;
import com.github.oryanmat.trellowidget.util.TrelloAPIUtil;
import com.github.oryanmat.trellowidget.widget.TrelloWidgetProvider;

/**
 * This activity pops up a dialog to move a given card
 *
 * Created by jramsay on 6/7/2016.
 */
public class MoveCardActivity extends Activity {
    public static final String EXTRA_NEXTPOS = "com.github.oryanmat.trellowidget.nextPos";
    public static final String EXTRA_PREVPOS = "com.github.oryanmat.trellowidget.prevPos";

    private Card card;
    private int appWidgetId;
    private String nextPos;
    private String prevPos;

    static public Intent createMoveCardIntent(Context context, Bundle extras)
    {
        Intent moveCardIntent = new Intent(TrelloWidgetProvider.MOVE_CARD_ACTION, Uri.EMPTY, context, MoveCardActivity.class);
        moveCardIntent.putExtras(extras);
        return moveCardIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        card = Card.parse(extras.getString(TrelloWidgetProvider.EXTRA_CARD));
        appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        nextPos = extras.getString(EXTRA_NEXTPOS);
        prevPos = extras.getString(EXTRA_PREVPOS);

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
        if (prevPos.isEmpty() || prevPos.equals(TrelloAPIUtil.CARDS_POSITION_TOP)) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToTop();
                }
            });
        }

        button = (Button)findViewById(R.id.move_card_up_button);
        if (prevPos.isEmpty()) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveUp();
                }
            });
        }

        button = (Button) findViewById(R.id.move_card_down_button);
        if (nextPos.isEmpty()) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveDown();
                }
            });
        }

        button = (Button)findViewById(R.id.move_card_bottom_button);
        if (nextPos.isEmpty() || nextPos.equals(TrelloAPIUtil.CARDS_POSITION_BOTTOM)) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToBottom();
                }
            });
        }

        ImageButton listMove = (ImageButton)findViewById(R.id.move_card_list_button);
        listMove.setEnabled(false);
        listMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToList();
            }
        });

        Spinner listSelection = (Spinner)findViewById(R.id.move_card_list_selection);
        // TODO: Consider re-fetching this instead of using the cached values, in case these are not current?
        Board board = TrelloWidget.getBoard(this, appWidgetId);
        ArrayAdapter<BoardList> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, board.lists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSelection.setAdapter(adapter);
        int selectedIndex = ConfigActivity.getSelectedIndex(board.lists, TrelloWidget.getList(this, appWidgetId));
        // TODO: Consider making 'selectedIndex + 1' the default selection?
        if (selectedIndex > -1) {
            listSelection.setSelection(selectedIndex);
        }
        listSelection.setOnItemSelectedListener(new ListSelectionListener(listMove, selectedIndex));
    }

    private class ListSelectionListener implements AdapterView.OnItemSelectedListener {
        private ImageButton goButton;
        private int currentPosition;

        ListSelectionListener(ImageButton button, int currentSelection) {
            goButton = button;
            currentPosition = currentSelection;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == currentPosition) {
                goButton.setEnabled(false);
            } else {
                goButton.setEnabled(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            goButton.setEnabled(false);
        }
    }

    protected void moveToTop()
    {
        Log.d(TrelloWidget.T_WIDGET, "Moving card " + card.toString() + " to top");
        TrelloAPIUtil.instance.repositionCard(card, TrelloAPIUtil.CARDS_POSITION_TOP, new MoveListener());
    }

    protected void moveUp()
    {
        Log.d(TrelloWidget.T_WIDGET, "Moving card " + card.toString() + " up to " + prevPos);
        if (prevPos.isEmpty()) {
            close(false);
        } else {
            TrelloAPIUtil.instance.repositionCard(card, prevPos, new MoveListener());
        }
    }

    protected void moveDown()
    {
        Log.d(TrelloWidget.T_WIDGET, "Moving card " + card.toString() + " down to " + nextPos);
        if (nextPos.isEmpty()) {
            close(false);
        } else {
            TrelloAPIUtil.instance.repositionCard(card, nextPos, new MoveListener());
        }
    }

    protected void moveToBottom()
    {
        Log.d(TrelloWidget.T_WIDGET, "Moving card " + card.toString() + " to bottom");
        TrelloAPIUtil.instance.repositionCard(card, TrelloAPIUtil.CARDS_POSITION_BOTTOM, new MoveListener());
    }

    protected void moveToList()
    {
        Spinner listSelection = (Spinner)findViewById(R.id.move_card_list_selection);
        BoardList destination = (BoardList)listSelection.getSelectedItem();
        if (destination.id.equals("-1")) {
            Log.d(TrelloWidget.T_WIDGET, "Could not find destination board - Not moving");
            close(false);
        } else {
            Log.d(TrelloWidget.T_WIDGET, "Moving card " + card.toString() + " to " + destination.name + " (" + destination.id + ")");
            TrelloAPIUtil.instance.moveCardToList(card, destination, new MoveListener());
        }
    }

    private class MoveListener extends TrelloAPIUtil.CardResponseListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            logError("Move request failed", error);

            String message = getString(R.string.move_card_failure);
            if (error.networkResponse.statusCode == 401) {
                // TODO: Maybe actually open the login dialog?
                message = getString(R.string.move_card_permission_failure);
            }
            Toast.makeText(MoveCardActivity.this, message, Toast.LENGTH_LONG).show();
            close(false);
        }

        @Override
        public void onResponse(Card card) {
            Log.d(TrelloWidget.T_WIDGET, "Move request succeeded");
            Toast.makeText(MoveCardActivity.this, getString(R.string.move_card_success), Toast.LENGTH_SHORT).show();
            close(true);
        }
    }

    protected void close(boolean needsReload)
    {
        finish();
        if (needsReload) {
            Intent refreshIntent = IntentUtil.createRefreshIntent(this, appWidgetId);
            sendBroadcast(refreshIntent);
        }
    }
}
