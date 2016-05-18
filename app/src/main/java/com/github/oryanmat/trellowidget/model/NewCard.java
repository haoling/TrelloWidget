package com.github.oryanmat.trellowidget.model;

/**
 * Corresponds to the POST request for Trello's /1/cards API
 * Created by jramsay on 5/18/2016.
 */
public class NewCard {
    public static final String POS_TOP = "top";
    public static final String POS_BOTTOM = "bottom";

    public String name; // Optional
    public String desc; // Optional
    public String pos; // Optional; may be POS_TOP, POS_BOTTOM, or a positive integer
    public String due; // required (may be null)
    public String idList; // required (ID of list to add the card to)
    public String idMembers; // Optional; comma-delimited list
    public String idLabels; // Optional; comma-delimited list
    public String urlSource; // Optional
    public String fileSource; // Optional

    // These are for copying a card contents from another card
    public String idCardSource; // Optional; The card to copy from
    public String keepFromSource; // Optional; Properties of the source card to copy

    public NewCard(String targetList, String cardName) {
        idList = targetList;
        name = cardName;
    }

    public NewCard atTop() {
        pos = POS_TOP;
        return this;
    }

    public NewCard atBottom() {
        pos = POS_BOTTOM;
        return this;
    }
}
