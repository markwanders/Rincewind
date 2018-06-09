package com.example.mndmw.rincewind;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements AccountAdapter.AccountClickListener, LoaderManager.LoaderCallbacks<List<Account>> {

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;

    private AccountAdapter mAccountAdapter;

    private TextView mErrorMessageDisplay;

    private static final String TYPE = "type";

    private static final String ID = "id";

    private static final int ACCOUNTS_LOADER_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_accounts);

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAccountAdapter = new AccountAdapter(getApplicationContext(), this);

        mRecyclerView.setAdapter(mAccountAdapter);

        mLoadingIndicator = findViewById(R.id.loading_indicator);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        LoaderManager.LoaderCallbacks<List<Account>> callback = MainActivity.this;

        getSupportLoaderManager().initLoader(ACCOUNTS_LOADER_ID, null, callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int selectedMenuItem = menuItem.getItemId();
        if(selectedMenuItem == R.id.action_get) {
            getSupportLoaderManager().restartLoader(ACCOUNTS_LOADER_ID, null, this);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onAccountClick(String type, String id) {
        Context context = this;
        Class destinationClass = TransactionsActivity.class;
        Intent intent = new Intent(context, destinationClass);
        Bundle extras = new Bundle();
        extras.putString(TYPE, type);
        extras.putString(ID, id);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public Loader<List<Account>> onCreateLoader(int id, final Bundle args) {


        return new Loader<List<Account>>(this) {
            List<Account> accounts = null;

            @Override
            protected void onStartLoading() {
                if (accounts == null) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
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

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                AccountsRequest accountsRequest = new AccountsRequest(new Response.Listener<List<Account>>() {
                    @Override
                    public void onResponse(List<Account> response) {
                        deliverResult(response);
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage();
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                    }
                });

                queue.add(accountsRequest);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Account>> loader, List<Account> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mAccountAdapter.setAccountsData(data);
        if(null == data) {
            showErrorMessage();
        } else {
            showAccountsView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Account>> loader) {

    }

    private void showAccountsView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private class AccountsRequest extends JsonRequest<List<Account>> {
        private static final String URL = "https://lavaeolus.herokuapp.com/api/accounts";

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>(super.getHeaders());
            headers.put("X-Auth-Token", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", ""));
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
