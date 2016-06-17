package com.github.oryanmat.trellowidget.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.oryanmat.trellowidget.R;
import com.github.oryanmat.trellowidget.TrelloWidget;
import com.github.oryanmat.trellowidget.model.BoardList;
import com.github.oryanmat.trellowidget.model.Card;
import com.github.oryanmat.trellowidget.model.CardArray;
import com.github.oryanmat.trellowidget.model.NewCard;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.github.oryanmat.trellowidget.TrelloWidget.INTERNAL_PREFS;
import static com.github.oryanmat.trellowidget.TrelloWidget.T_WIDGET;

public class TrelloAPIUtil {
    public static final String TOKEN_PREF_KEY = "com.oryanmat.trellowidget.usertoken";

    public static final String APP_KEY = "b250ef70ccf79ea5e107279a91045e6e";
    public static final String BASE_URL = "https://api.trello.com/";
    public static final String API_VERSION = "1/";
    public static final String KEY = String.format("key=%s", APP_KEY);
    public static final String AUTH_URL = "https://trello.com/1/authorize" +
            "?name=TrelloWidget" +
            "&" + KEY +
            "&scope=read,write" +
            "&expiration=never" +
            "&callback_method=fragment" +
            "&return_url=trello-widget://callback";

    public static final String USER = "members/me?fields=fullName,username&";
    public static final String BOARDS = "members/me/boards?filter=open&fields=id,name,url" +
            "&lists=open&list_fields=id,name&";
    public static final String LIST_CARDS = "lists/%s?cards=open&card_fields=name,badges,labels,url,pos&";
    public static final String CARDS = "cards/?";
    public static final String CARDS_POSITION = "cards/%s/pos?value=%s&";
    public static final String MOVE_CARD_TO_LIST = "cards/%s/idList?value=%s&";

    public static final String CARDS_POSITION_TOP = "top";
    public static final String CARDS_POSITION_BOTTOM = "bottom";

    public static TrelloAPIUtil instance;

    Context context;
    RequestQueue queue;
    SharedPreferences preferences;

    private TrelloAPIUtil(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(INTERNAL_PREFS, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        instance = new TrelloAPIUtil(context);
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    public String buildURL() {
        String token = preferences.getString(TrelloAPIUtil.TOKEN_PREF_KEY, "");
        return BASE_URL + API_VERSION + "%s" + KEY + "&" + token;
    }

    public String user() {
        return String.format(buildURL(), USER);
    }

    public String boards() {
        return String.format(buildURL(), BOARDS);
    }

    public CardArray getCards(BoardList list) {
        String json = get(String.format(String.format(buildURL(), LIST_CARDS), list.id));

        return Json.tryParseJson(json, CardArray.class, CardArray.oneItemList(json));
    }

    public <L extends Response.Listener<String> & Response.ErrorListener> void addNewCard(NewCard newCard, L listener) {
        String json = Json.get().toJson(newCard);
        postAsync(String.format(buildURL(), CARDS), json, listener);
    }

    public <L extends Response.Listener<String> & Response.ErrorListener> void repositionCard(Card card, String pos, L listener) {
        putAsync(String.format(String.format(buildURL(), CARDS_POSITION), card.id, pos), listener);
    }

    public <L extends Response.Listener<String> & Response.ErrorListener> void moveCardToList(Card card, BoardList list, L listener) {
        putAsync(String.format(String.format(buildURL(), MOVE_CARD_TO_LIST), card.id, list.id), listener);
    }

    String get(String url) {
        return syncRequest(url, null, Request.Method.GET);
    }

    String syncRequest(String url, String data, int method) {
        RequestFuture<String> future = RequestFuture.newFuture();
        requestAsync(url, data, method, future, future);
        return get(future);
    }

    String get(RequestFuture<String> future) {
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            String msg = String.format(context.getString(R.string.http_fail), e);
            Log.e(T_WIDGET, msg);
            return msg;
        }
    }

    public <L extends Response.Listener<String> & Response.ErrorListener> void getAsync(String url, L listener) {
        requestAsync(url, null, Request.Method.GET, listener, listener);
    }

    public void getAsync(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        requestAsync(url, null, Request.Method.GET, listener, errorListener);
    }

    public <L extends Response.Listener<String> & Response.ErrorListener> void postAsync(String url, String data, L listener) {
        requestAsync(url, data, Request.Method.POST, listener, listener);
    }

    public <L extends Response.Listener<String> & Response.ErrorListener> void putAsync(String url, L listener) {
        requestAsync(url, null, Request.Method.PUT, listener, listener);
    }

    // TODO: Since we're dealing exclusively with JSON here, maybe it would be easier to start using JsonObjectRequest objcets as opposed to plain StringRequest objects
    public void requestAsync(String url, final String data, int method, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(method, url, listener, errorListener) {
            @Override
            public byte[] getBody() {
                if (data != null) {
                    return data.getBytes();
                }
                return new byte[0];
            }

            @Override
            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }
        };
        getRequestQueue().add(request);
    }

    /**
     * Compute the position value needed to move a given card up by one in the card list
     *
     * @param cards The list of cards
     * @param position The position in the list of the card we want to move up by one
     * @return Empty String if this is at the top of the list, CARDS_POSITION_TOP if the only way up is to go to the top, or the String representation of the value that should be used to move the card up by 1
     */
    public static String getPrevPos(List<Card> cards, int position) {
        // Trello's API is odd - To move a card up by one, you need to find the position of the previous card and the one previous to that, and find a number in between.
        String prevPos = "";
        if (position > 1) {
            Double onePrev = Double.parseDouble(cards.get(position - 1).pos);
            Double twoPrev = Double.parseDouble(cards.get(position - 2).pos);
            Double halfWay = onePrev + ((twoPrev - onePrev) / 2);
            prevPos = halfWay.toString();
        } else if (position == 1) {
            prevPos = CARDS_POSITION_TOP;
        }
        return prevPos;
    }

    /**
     * Compute the position value needed to move a given card down by one in the card list
     *
     * @param cards The list of cards
     * @param position The position in the list of the card we want to move down by one
     * @return Empty String if this is at the bottom of the list, CARDS_POSITION_BOTTOM if the only way down is to go to the bottom, or the String representation of the value that should be used to move the card down by 1
     */
    public static String getNextPos(List<Card> cards, int position) {
        // Trello's API is odd - To move a card down by one, you need to find the position of the next card and the one after that, and find a number in between.
        String nextPos = "";
        if (position < (cards.size() - 2)) {
            Double oneNext = Double.parseDouble(cards.get(position + 1).pos);
            Double twoNext = Double.parseDouble(cards.get(position + 2).pos);
            Double halfWay = oneNext + ((twoNext - oneNext) / 2);
            nextPos = halfWay.toString();
        } else if (position < (cards.size() - 1)) {
            nextPos = TrelloAPIUtil.CARDS_POSITION_BOTTOM;
        }
        return nextPos;
    }

    /**
     * Overrides the default String Listener and automatically takes care of parsing the response json into a Card object
     */
    public static abstract class CardResponseListener implements Response.Listener<String>, Response.ErrorListener {

        public abstract void onResponse(Card newCard);

        @Override
        public void onResponse(String response) {
            Card card = Json.tryParseJson(response, Card.class, new Card());
            onResponse(card);
        }

        public void logError(String message, VolleyError error) {
            String errorResponse = message + " (" + error.networkResponse.statusCode + ")";
            try {
                String responseData = new String(error.networkResponse.data, "UTF-8");
                errorResponse += ": " + responseData;
            } catch (UnsupportedEncodingException e) {
                Log.w(TrelloWidget.T_WIDGET, "Could not decode error.networkResponse.data", e);
            }
            Log.e(TrelloWidget.T_WIDGET, errorResponse);
        }
    }
}
