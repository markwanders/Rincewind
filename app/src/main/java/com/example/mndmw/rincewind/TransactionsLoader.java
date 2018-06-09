package com.example.mndmw.rincewind;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.mndmw.rincewind.domain.Account;
import com.example.mndmw.rincewind.domain.Transaction;
import com.example.mndmw.rincewind.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yo95gv on 13-2-2018.
 */

public class TransactionsLoader extends Loader<List<Transaction>> {
    private List<Transaction> transactions = null;
    private Bundle args;
    private ProgressBar mLoadingIndicator;

    private final static String BASE = "https://lavaeolus.herokuapp.com/api/";
    private final static String ACCOUNTS = "accounts/";
    private final static String TRANSACTIONS = "transactions/";
    private static final String ID = "id";
    private static final String TYPE = "type";

    public TransactionsLoader(Context context, Bundle args, ProgressBar mLoadingIndicator) {
        super(context);
        this.mLoadingIndicator = mLoadingIndicator;
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (transactions == null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
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

        TransactionsRequest transactionsRequest = new TransactionsRequest(createUrl(), new Response.Listener<List<Transaction>>() {
            @Override
            public void onResponse(List<Transaction> response) {
                deliverResult(response);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                showErrorMessage();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        });

        queue.add(transactionsRequest);
    }

    public void deliverResult(List<Transaction> transactionsResult) {
        this.transactions = transactionsResult;
        super.deliverResult(transactionsResult);
    }

    private class TransactionsRequest extends JsonRequest<List<Transaction>> {

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>(super.getHeaders());
            headers.put("X-Auth-Token", PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token", ""));
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
