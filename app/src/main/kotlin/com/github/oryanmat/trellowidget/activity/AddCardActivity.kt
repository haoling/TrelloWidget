package com.github.oryanmat.trellowidget.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log

import com.github.oryanmat.trellowidget.R

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import com.github.oryanmat.trellowidget.T_WIDGET
import com.github.oryanmat.trellowidget.util.getBoard
import com.github.oryanmat.trellowidget.util.getList
import kotlinx.android.synthetic.main.activity_add_card.*

class AddCardActivity : Activity() {
    internal var appWidgetId = INVALID_APPWIDGET_ID

    enum class Location {
        INSERT_AT_TOP,
        INSERT_AT_BOTTOM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
        }
        if (appWidgetId == INVALID_APPWIDGET_ID) {
            Log.e(T_WIDGET, "Invalid widget ID: Cannot add a new card")
            finish()
        }

        val list = getList(appWidgetId)
        val board = getBoard(appWidgetId)

        add_card_board_name.text = "${board.name} / ${list.name}"
        addCloseButton.setOnClickListener { v -> close() }
        topButton.setOnClickListener { v -> addNewCard(Location.INSERT_AT_TOP) }
        bottomButton.setOnClickListener { v -> addNewCard(Location.INSERT_AT_BOTTOM) }
    }

    fun addNewCard(where: Location) {
        val newTitle = add_card_title.text.toString()
        if (newTitle.isEmpty()) {
            return
        }
        add_card_title.setText("")

        val list = getList(appWidgetId)
        val board = getBoard(appWidgetId)
        // TODO: Actually do the add

        Log.i(T_WIDGET, "Would add new card to " + (if (where == Location.INSERT_AT_TOP) "top" else "bottom") + " of ${board.name}/${list.name}: $newTitle")

        // TODO: Optionally finish(); if the user only wants to add one card
    }

    fun close() {
        finish() // TODO: Send a widget refresh broadcast
    }
}
