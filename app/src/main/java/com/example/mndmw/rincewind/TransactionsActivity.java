package com.example.mndmw.rincewind;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private static final int TRANSACTIONS_LOADER_ID = 1;

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

        final Intent intentThatStartedThisActivity = getIntent();

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        LoaderManager.LoaderCallbacks<List<Transaction>> callback = TransactionsActivity.this;

        mTransactionLoader = getSupportLoaderManager().initLoader(TRANSACTIONS_LOADER_ID, intentThatStartedThisActivity.getExtras(), callback);
    }

    @Override
    public Loader<List<Transaction>> onCreateLoader(int id, final Bundle args) {
        return new TransactionsLoader(this, args, mLoadingIndicator);
    }

    @Override
    public void onLoadFinished(Loader<List<Transaction>> loader, List<Transaction> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mTransactionAdapter.setTransactionData(data);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy, hh:mm:ss" );
        mLastUpdatedTextView.setText(getString(R.string.last_updated, simpleDateFormat.format(Calendar.getInstance().getTime())));
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Transaction>> loader) {

    }

    @Override
    public void onRefresh() {
        mTransactionLoader.forceLoad();
    }
}