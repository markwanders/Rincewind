package com.example.mndmw.rincewind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TransactionsActivity extends AppCompatActivity {

    private String mType;
    private TextView mTransactionsDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        mTransactionsDisplay = (TextView) findViewById(R.id.tv_display_transactions);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mType = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mTransactionsDisplay.setText(mType);
            }
        }
    }
}
