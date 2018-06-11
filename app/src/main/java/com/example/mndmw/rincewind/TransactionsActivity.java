package com.example.mndmw.rincewind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mndmw.rincewind.domain.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Transaction>>, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private TransactionAdapter mTransactionAdapter;
    private Loader<List<Transaction>> mTransactionLoader;
    private TextView mLastUpdatedTextView;
    private Bundle args;

    private static SharedPreferences sharedPreferences;

    private static final int TRANSACTIONS_LOADER_ID = 1;

    private static final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        mRecyclerView = findViewById(R.id.rv_transactions);

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mTransactionAdapter = new TransactionAdapter(getApplicationContext());

        mRecyclerView.setAdapter(mTransactionAdapter);

        mLoadingIndicator = findViewById(R.id.loading_indicator);

        mLastUpdatedTextView = findViewById(R.id.transaction_last_updated);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final Intent intentThatStartedThisActivity = getIntent();
        args = intentThatStartedThisActivity.getExtras();

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        LoaderManager.LoaderCallbacks<List<Transaction>> callback = TransactionsActivity.this;

        mTransactionLoader = getSupportLoaderManager().initLoader(TRANSACTIONS_LOADER_ID, args, callback);
    }

    @Override
    public Loader<List<Transaction>> onCreateLoader(int id, final Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        return new TransactionsLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Transaction>> loader, List<Transaction> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);

        if(data == null) {
            goToLogin();
        }

        mTransactionAdapter.setTransactionData(data);
        mLastUpdatedTextView.setText(getString(R.string.last_updated, SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())));
    }

    @Override
    public void onLoaderReset(Loader<List<Transaction>> loader) {

    }

    @Override
    public void onRefresh() {
        mTransactionLoader.forceLoad();
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
            getSupportLoaderManager().restartLoader(TRANSACTIONS_LOADER_ID, args, this);
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

    private void goToLogin() {
        Class destinationClass = LoginActivity.class;
        Intent intent = new Intent(getApplicationContext(), destinationClass);
        startActivity(intent);
        finish();
    }
}