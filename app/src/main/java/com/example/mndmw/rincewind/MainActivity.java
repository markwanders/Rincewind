package com.example.mndmw.rincewind;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mndmw.rincewind.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mResultsTextView;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultsTextView = (TextView) findViewById(R.id.address);

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
            new GetTask().execute(NetworkUtils.buildUrl());
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class GetTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String getResults = null;
            try {
                getResults = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return getResults;
        }

        @Override
        protected void onPostExecute(String getResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (getResults != null && !getResults.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(getResults);
                    mResultsTextView.setText("");
                    mResultsTextView.append("Address: ");
                    mResultsTextView.append(jsonObject.getString("accountAddress"));
                    mResultsTextView.append("\n");
                    mResultsTextView.append("Balance: ");
                    mResultsTextView.append(String.valueOf(jsonObject.getLong("accountBalance")));
                    mResultsTextView.append(" wei");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
