package com.example.mndmw.rincewind.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.mndmw.rincewind.Constants;
import com.example.mndmw.rincewind.models.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yo95gv on 13-2-2018.
 */

public class TransactionsLoader extends Loader<List<Transaction>> {
    private List<Transaction> transactions = null;
    private Bundle args;

    private final static String BASE = Constants.BASE_URL + "/api/";
    private final static String ACCOUNTS = "accounts/";
    private final static String TRANSACTIONS = "transactions/";
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String TOKEN_KEY = "token";

    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


    public TransactionsLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (transactions == null) {
            forceLoad();
        } else {
            deliverResult(transactions);
        }
    }

    private String createUrl() {
        if(args != null && args.containsKey(ID) && args.containsKey(TYPE)) {
            return BASE + ACCOUNTS + args.getString(TYPE) + "/" + args.getString(ID) + "/" + TRANSACTIONS;
        } else {
            return BASE + ACCOUNTS + TRANSACTIONS;
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        TransactionsRequest transactionsRequest = new TransactionsRequest(createUrl(), this::deliverResult, error -> {
            NetworkResponse networkResponse = error.networkResponse;
            if(networkResponse.statusCode == 403 || networkResponse.statusCode == 401) {
                //Token is invalid/expired, delete it so we can log in again
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(TOKEN_KEY);
                editor.apply();
            }
            deliverResult(null);
        });

        queue.add(transactionsRequest);
    }

    public void deliverResult(List<Transaction> transactionsResult) {
        this.transactions = transactionsResult;
        super.deliverResult(transactionsResult);
    }

    private class TransactionsRequest extends JsonRequest<List<Transaction>> {
        private static final String TOKEN_HEADER = "X-Auth-Token";

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>(super.getHeaders());
            headers.put(TOKEN_HEADER, sharedPreferences.getString(TOKEN_KEY, ""));
            return headers;
        }

        public TransactionsRequest(String url, Response.Listener<List<Transaction>> listener, Response.ErrorListener errorListener) {
            super(Method.GET, url, null, listener, errorListener);
        }

        @Override
        protected Response<List<Transaction>> parseNetworkResponse(NetworkResponse response) {
            try {
                Gson gson = new Gson();
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                List<Transaction> transactions = gson.fromJson(json,  new TypeToken<List<Transaction>>(){}.getType());
                return Response.success(transactions, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new VolleyError(e));
            }
        }
    }

}
