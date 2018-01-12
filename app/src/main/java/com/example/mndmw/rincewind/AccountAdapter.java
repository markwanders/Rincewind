package com.example.mndmw.rincewind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mndmw.rincewind.domain.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mndmw on 10-1-2018.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountAdapterViewHolder> {
    private List<Account> mAccountData = new ArrayList<>();

    @Override
    public AccountAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForAccountData = R.layout.account_data;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForAccountData, parent, false);
        return new AccountAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountAdapterViewHolder holder, int position) {
        Account accountData = mAccountData.get(position);
        holder.bind(accountData);
    }

    @Override
    public int getItemCount() {
        if (null == mAccountData) return 0;
        return mAccountData.size();
    }

    void setAccountData(List<Account> accountData) {
        mAccountData = accountData;
        notifyDataSetChanged();
    }

    class AccountAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView mAccountTypeView;
        final TextView mAccountIdentifierNameView;
        final TextView mAccountIdentifierValueView;
        final TextView mAccountBalanceValueView;
        final TextView mAccountBalanceCurrencyView;

        AccountAdapterViewHolder(View itemView) {
            super(itemView);
            mAccountTypeView = itemView.findViewById(R.id.account_type);
            mAccountIdentifierNameView = itemView.findViewById(R.id.account_identifier_name);
            mAccountIdentifierValueView = itemView.findViewById(R.id.account_identifier_vale);
            mAccountBalanceValueView = itemView.findViewById(R.id.balance_value);
            mAccountBalanceCurrencyView = itemView.findViewById(R.id.balance_currency);
        }

        void bind(Account account) {
            mAccountTypeView.setText(account.getType());
            mAccountIdentifierNameView.setText(account.getIdentifiers().get(0).getName());
            mAccountIdentifierValueView.setText(account.getIdentifiers().get(0).getValue());
            mAccountBalanceValueView.setText(account.getBalances().get(0).getAmount().toString());
            mAccountBalanceCurrencyView.setText(account.getBalances().get(0).getCurrency());
        }
    }
}
