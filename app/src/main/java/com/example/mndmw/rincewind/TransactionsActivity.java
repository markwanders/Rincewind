package com.example.mndmw.rincewind;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class TransactionsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Transaction>> {

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

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_transactions);

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mTransactionAdapter = new TransactionAdapter(getApplicationContext());

        mRecyclerView.setAdapter(mTransactionAdapter);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(TYPE)) {
                mType = intentThatStartedThisActivity.getStringExtra(TYPE);
            }
            if (intentThatStartedThisActivity.hasExtra(ID)) {
                mID = intentThatStartedThisActivity.getStringExtra(ID);
            }
        }

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
                    forceLoad();
                } else {
                    deliverResult(transactions);
                }
            }

            @Override
            public List<Transaction> loadInBackground() {
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

            public void deliverResult(List<Transaction> transactionsResult) {
                this.transactions = transactionsResult;
                super.deliverResult(transactionsResult);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Transaction>> loader, List<Transaction> data) {
        mTransactionAdapter.setTransactionData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Transaction>> loader) {

    }
}
