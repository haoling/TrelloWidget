package com.github.oryanmat.trellowidget.activity

import android.app.Activity
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.github.oryanmat.trellowidget.R
import com.github.oryanmat.trellowidget.T_WIDGET
import com.github.oryanmat.trellowidget.model.Card
import com.github.oryanmat.trellowidget.util.createRefreshIntent
import com.github.oryanmat.trellowidget.widget.EXTRA_CARD
import com.github.oryanmat.trellowidget.widget.MOVE_CARD_ACTION

/**
 * This activity pops up a dialog to move a given card

 * Created by jramsay on 6/7/2016.
 */
class MoveCardActivity : Activity() {
    private var card: Card = Card()
    private var appWidgetId: Int = 0
    private val cardMoved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        card = Card.parse(extras.getString(EXTRA_CARD))
        appWidgetId = extras.getInt(EXTRA_APPWIDGET_ID)

        setContentView(R.layout.activity_move_card)
        setupTitle(card.name)
        setupButtons()
    }

    protected fun setupTitle(cardName: String) {
        val text = findViewById(R.id.move_card_card_name) as TextView
        text.text = cardName
    }

    protected fun setupButtons() {
        var button = findViewById(R.id.move_card_top_button) as Button
        button.setOnClickListener { moveToTop() }
        button = findViewById(R.id.move_card_up_button) as Button
        button.setOnClickListener { moveUp() }
        button = findViewById(R.id.move_card_list_button) as Button
        button.setOnClickListener { moveToList() }
        button = findViewById(R.id.move_card_down_button) as Button
        button.setOnClickListener { moveDown() }
        button = findViewById(R.id.move_card_bottom_button) as Button
        button.setOnClickListener { moveToBottom() }
    }

    protected fun moveToTop() {
        Log.d(T_WIDGET, "Would move card $card to top")
        close()
    }

    protected fun moveUp() {
        Log.d(T_WIDGET, "Would move card $card up by one")
        close()
    }

    protected fun moveDown() {
        Log.d(T_WIDGET, "Would move card $card down by one")
        close()
    }

    protected fun moveToBottom() {
        Log.d(T_WIDGET, "Would move card $card to bottom")
        close()
    }

    protected fun moveToList() {
        Log.d(T_WIDGET, "Would move card $card to ... some other list")
        close()
    }

    protected fun close() {
        finish()
        if (cardMoved) {
            val refreshIntent = createRefreshIntent(appWidgetId)
            sendBroadcast(refreshIntent)
        }
    }

    companion object {
        fun createMoveCardIntent(context: Context, card: Card, appWidgetId: Int): Intent {
            val moveCardIntent = Intent(MOVE_CARD_ACTION, Uri.EMPTY, context, MoveCardActivity::class.java)
            moveCardIntent.putExtra(EXTRA_CARD, card.toJson())
            moveCardIntent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
            return moveCardIntent
        }
    }
}
