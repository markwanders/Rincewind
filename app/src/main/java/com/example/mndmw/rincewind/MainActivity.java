package com.example.mndmw.rincewind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.mndmw.rincewind.domain.Account;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AccountAdapter.AccountClickListener, LoaderManager.LoaderCallbacks<List<Account>> {

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;

    private AccountAdapter mAccountAdapter;

    private TextView mErrorMessageDisplay;

    private static final String TYPE = "type";

    private static final String ID = "id";

    private static final String TOKEN_KEY = "token";

    private static final int ACCOUNTS_LOADER_ID = 0;

    private static SharedPreferences sharedPreferences;


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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
        if(selectedMenuItem == R.id.action_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(TOKEN_KEY);
            editor.apply();
            goToLogin();
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
        mLoadingIndicator.setVisibility(View.VISIBLE);

        return new AccountsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Account>> loader, List<Account> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mAccountAdapter.setAccountsData(data);
        if(null == data) {
            if(!sharedPreferences.contains(TOKEN_KEY)) {
                goToLogin();
            } else {
                showErrorMessage();
            }
        } else {
            showAccountsView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Account>> loader) {

    }

    private void goToLogin() {
        Class destinationClass = LoginActivity.class;
        Intent intent = new Intent(getApplicationContext(), destinationClass);
        startActivity(intent);
        finish();
    }

    private void showAccountsView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the account data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


}
