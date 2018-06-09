package com.example.mndmw.rincewind;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.mndmw.rincewind.domain.Account;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsLoader extends Loader<List<Account>> {
    private List<Account> accounts = null;

    private static final String TOKEN_KEY = "token";

    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public AccountsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (accounts == null) {
            forceLoad();
        } else {
            deliverResult(accounts);
        }
    }

    @Override
    public void deliverResult(List<Account> accountsResult) {
        this.accounts = accountsResult;
        super.deliverResult(accountsResult);
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        AccountsRequest accountsRequest = new AccountsRequest(new Response.Listener<List<Account>>() {
            @Override
            public void onResponse(List<Account> response) {
                deliverResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse.statusCode == 403 || networkResponse.statusCode == 401) {
                    //Token is invalid/expired, delete it so we can log in again
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(TOKEN_KEY);
                    editor.apply();                }
                deliverResult(null);
            }
        });

        queue.add(accountsRequest);

    }

    private class AccountsRequest extends JsonRequest<List<Account>> {
        private static final String URL = "https://lavaeolus.herokuapp.com/api/accounts";
        private static final String TOKEN_HEADER = "X-Auth-Token";

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>(super.getHeaders());
            headers.put(TOKEN_HEADER,sharedPreferences.getString(TOKEN_KEY, ""));
            return headers;
        }

        AccountsRequest(Response.Listener<List<Account>> listener, Response.ErrorListener errorListener) {
            super(Method.GET, URL, null, listener, errorListener);
        }

        @Override
        protected Response<List<Account>> parseNetworkResponse(NetworkResponse response) {
            try {
                Gson gson = new Gson();
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                List<Account> accountsResult = gson.fromJson(json,  new TypeToken<List<Account>>(){}.getType());
                return Response.success(accountsResult, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new VolleyError(e));
            }
        }
    }
}
