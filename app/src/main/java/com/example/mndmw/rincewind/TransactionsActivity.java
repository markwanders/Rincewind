package com.example.mndmw.rincewind;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mndmw.rincewind.domain.Account;
import com.example.mndmw.rincewind.domain.Transaction;
import com.example.mndmw.rincewind.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Transaction>>, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private TransactionAdapter mTransactionAdapter;

    private String mType;
    private String mID;

    private static final String TYPE = "type";
    private static final String ID = "id";
    private final static String ACCOUNTS = "accounts";
    private final static String TRANSACTIONS = "transactions";

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

        final Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(TYPE)) {
                mType = intentThatStartedThisActivity.getStringExtra(TYPE);
            }
            if (intentThatStartedThisActivity.hasExtra(ID)) {
                mID = intentThatStartedThisActivity.getStringExtra(ID);
            }
        }

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        LoaderManager.LoaderCallbacks<List<Transaction>> callback = TransactionsActivity.this;

        getSupportLoaderManager().initLoader(TRANSACTIONS_LOADER_ID, intentThatStartedThisActivity.getExtras(), callback);
    }

    @Override
    public Loader<List<Transaction>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Transaction>>(this) {
            List<Transaction> transactions = null;

            @Override
            protected void onStartLoading() {
                if (transactions == null) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                } else {
                    deliverResult(transactions);
                }
            }

            @Override
            public List<Transaction> loadInBackground() {
                return loadTransactions(args);
            }

            public void deliverResult(List<Transaction> transactionsResult) {
                this.transactions = transactionsResult;
                super.deliverResult(transactionsResult);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Transaction>> loader, List<Transaction> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mTransactionAdapter.setTransactionData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Transaction>> loader) {

    }

    @Override
    public void onRefresh() {
        loadTransactions(getIntent().getExtras());
    }

    private List<Transaction> loadTransactions(Bundle args) {
        URL url;
        if(args != null && args.containsKey(ID) && args.containsKey(TYPE)) {
            url = NetworkUtils.buildUrl(ACCOUNTS, args.getString(TYPE), TRANSACTIONS, args.getString(ID));
        } else {
            url = NetworkUtils.buildUrl(ACCOUNTS, TRANSACTIONS);
        }
        String resultString = null;
        List<Transaction> accounts = new ArrayList<>();
        try {
            resultString = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            List<Transaction> transactionsResult = gson.fromJson(resultString,  new TypeToken<List<Transaction>>(){}.getType());
            if (transactionsResult != null) {
                accounts.addAll(transactionsResult);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }
}
