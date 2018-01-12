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

import com.example.mndmw.rincewind.domain.Account;
import com.example.mndmw.rincewind.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;

    private AccountAdapter mAccountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_accounts);

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAccountAdapter = new AccountAdapter(getApplicationContext());

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
            new GetTask().execute(NetworkUtils.buildUrl("accounts"));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class GetTask extends AsyncTask<URL, Void, List<Account>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Account> doInBackground(URL... urls) {
            URL url = urls[0];
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
