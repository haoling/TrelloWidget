package com.github.oryanmat.trellowidget.model;

import com.github.oryanmat.trellowidget.util.Json;

public class Card {
    public String id;
    public String name;
    public String desc;
    public String due;
    public Badges badges;
    public String url;
    public Label[] labels;

    @Override
    public String toString()
    {
        return name + " (" + id + ")";
    }

    /**
     * Serialize this card to a JSON string
     *
     * @return The JSON that represents this card object
     */
    public String toJson()
    {
        return Json.get().toJson(this);
    }

    /**
     * Create a new Card object based on a JSON string
     *
     * @param json the JSON format string to parse
     * @return The corresponding Card object, or an empty Card object if the parse fails
     */
    public static Card parse(String json)
    {
        return Json.tryParseJson(json, Card.class, new Card());
    }
}
