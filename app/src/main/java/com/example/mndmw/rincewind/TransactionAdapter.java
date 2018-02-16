package com.example.mndmw.rincewind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mndmw.rincewind.domain.Account;
import com.example.mndmw.rincewind.domain.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mndmw on 10-1-2018.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionAdapterViewHolder> {
    private List<Transaction> mTransactionData = new ArrayList<>();

    private LayoutInflater mLayoutInflater;

    TransactionAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public TransactionAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForTransactionData = R.layout.transaction_data;

        View view = mLayoutInflater.inflate(layoutIdForTransactionData, parent, false);
        return new TransactionAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionAdapterViewHolder holder, int position) {
        Transaction transactionData = mTransactionData.get(position);
        holder.bind(transactionData);
    }

    @Override
    public int getItemCount() {
        if (null == mTransactionData) return 0;
        return mTransactionData.size();
    }

    void setTransactionData(List<Transaction> transactionData) {
        mTransactionData = transactionData;
        notifyDataSetChanged();
    }

    class TransactionAdapterViewHolder extends RecyclerView.ViewHolder {

        TransactionAdapterViewHolder(View itemView) {
            super(itemView);
        }

        void bind(Transaction transaction) {

            TextView mTransactionDescriptionView = itemView.findViewById(R.id.transaction_description);
            mTransactionDescriptionView.setText(transaction.getDescription());

            TextView mTransactionCounterPartyView = itemView.findViewById(R.id.transaction_counterParty);
            mTransactionCounterPartyView.setText(transaction.getCounterParty());

            TextView mTransactionAmountView = itemView.findViewById(R.id.transaction_amount);
            mTransactionAmountView.setText(String.format("%.2f %s", transaction.getAmount(), transaction.getCurrency()));

        }

    }
}
