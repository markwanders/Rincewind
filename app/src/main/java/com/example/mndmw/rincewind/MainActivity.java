package com.example.mndmw.rincewind;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mndmw.rincewind.domain.Account;
import com.example.mndmw.rincewind.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AccountAdapter.AccountClickListener, LoaderManager.LoaderCallbacks<List<Account>> {

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;

    private AccountAdapter mAccountAdapter;

    private TextView mErrorMessageDisplay;

    private final static String ACCOUNTS = "accounts";

    private static final String TYPE = "type";

    private Toast mToast;

    private static final int ACCOUNTS_LOADER_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_accounts);

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAccountAdapter = new AccountAdapter(getApplicationContext(), this);

        mRecyclerView.setAdapter(mAccountAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

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
    public void onAccountClick(String type) {
        Context context = this;
        Class destinationClass = TransactionsActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(Intent.EXTRA_TEXT, type);
        startActivity(intent);
    }

    @Override
    public Loader<List<Account>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Account>>(this) {
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
            public List<Account> loadInBackground() {
                URL url;
                if(args != null && args.containsKey(TYPE)) {
                    url = NetworkUtils.buildUrl(ACCOUNTS, args.getString(TYPE));
                } else {
                    url = NetworkUtils.buildUrl(ACCOUNTS);
                }
                String resultString = null;
                List<Account> accounts = new ArrayList<>();
                try {
                    resultString = NetworkUtils.getResponseFromHttpUrl(url);
                    Gson gson = new Gson();
                    List<Account> accountsResult = gson.fromJson(resultString,  new TypeToken<List<Account>>(){}.getType());
                    if (accountsResult != null) {
                        accounts.addAll(accountsResult);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return accounts;
            }

            public void deliverResult(List<Account> accountsResult) {
                this.accounts = accountsResult;
                super.deliverResult(accountsResult);
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

}
