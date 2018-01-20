package com.example.mndmw.rincewind;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mndmw.rincewind.domain.Account;
import com.example.mndmw.rincewind.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AccountAdapter.AccountClickListener {

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;

    private AccountAdapter mAccountAdapter;

    private final static String ACCOUNTS = "accounts";

    private Toast mToast;

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
            new GetAccountsTask().execute(ACCOUNTS);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onAccountClick(String type) {
        if(mToast != null) {
            mToast.cancel();
        }
        String message = "Refreshing " + type + " data";
        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        mToast.show();
        new GetAccountsTask().execute(ACCOUNTS, type);
    }

    public class GetAccountsTask extends AsyncTask<String, Void, List<Account>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Account> doInBackground(String... endpoints) {
            URL url = NetworkUtils.buildUrl(ACCOUNTS);
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

        @Override
        protected void onPostExecute(List<Account> accounts) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mAccountAdapter.setAccountData(accounts);
        }
    }
}
