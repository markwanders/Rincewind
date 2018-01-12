package com.example.mndmw.rincewind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mndmw.rincewind.domain.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mndmw on 10-1-2018.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountAdapterViewHolder> {
    private List<Account> mAccountData = new ArrayList<>();

    private LayoutInflater inflater;

    public AccountAdapter(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
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
        final LinearLayout mAccountIdentifierContainer;
        final LinearLayout mAccountBalanceContainer;


        AccountAdapterViewHolder(View itemView) {
            super(itemView);
            mAccountTypeView = itemView.findViewById(R.id.account_type);
            mAccountIdentifierContainer = itemView.findViewById(R.id.account_identifier_container);
            mAccountBalanceContainer = itemView.findViewById(R.id.account_balance_container);
        }

        void bind(Account account) {
            mAccountTypeView.setText(account.getType());

            for(Account.Identifier identifier : account.getIdentifiers()) {
                View view = inflater.inflate(R.layout.account_identifier, null);

                TextView mIdentifierNameView = view.findViewById(R.id.account_identifier_name);
                mIdentifierNameView.setText(identifier.getName());

                TextView mIdentifierValueView = view.findViewById(R.id.account_identifier_value);
                mIdentifierValueView.setText(identifier.getValue());

                mAccountIdentifierContainer.addView(view);
            }

            for(Account.Balance balance : account.getBalances()) {
                View view = inflater.inflate(R.layout.account_balance, null);

                TextView mIdentifierNameView = view.findViewById(R.id.balance_value);
                mIdentifierNameView.setText(balance.getAmount().toString());

                TextView mIdentifierValueView = view.findViewById(R.id.balance_currency);
                mIdentifierValueView.setText(balance.getCurrency());

                mAccountBalanceContainer.addView(view);
            }
        }
    }
}
