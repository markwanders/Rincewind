package com.example.mndmw.rincewind;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mndmw.rincewind.domain.Transaction;
import com.example.mndmw.rincewind.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yo95gv on 13-2-2018.
 */

public class TransactionsLoader extends android.support.v4.content.AsyncTaskLoader<List<Transaction>> {
    List<Transaction> transactions = null;
    private Bundle args;
    private ProgressBar mLoadingIndicator;

    private final static String ACCOUNTS = "accounts";
    private final static String TRANSACTIONS = "transactions";
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

    @Override
    public List<Transaction> loadInBackground() {
        URL url;
        if(args != null && args.containsKey(ID) && args.containsKey(TYPE)) {
            url = NetworkUtils.buildUrl(ACCOUNTS, args.getString(TYPE), args.getString(ID), TRANSACTIONS);
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
}
