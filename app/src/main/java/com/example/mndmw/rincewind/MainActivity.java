package com.example.mndmw.rincewind;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mndmw.rincewind.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultsTextView = (TextView) findViewById(R.id.address);
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
            if (getResults != null && !getResults.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(getResults);
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
