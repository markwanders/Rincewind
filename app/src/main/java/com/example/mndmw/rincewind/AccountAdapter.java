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

    private LayoutInflater mLayoutInflater;

    final private AccountClickListener mAccountClickListener;

    public interface AccountClickListener {
        void onAccountClick(String type, String id);
    }

    AccountAdapter(Context context, AccountClickListener accountClickListener) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAccountClickListener = accountClickListener;
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

    void setAccountsData(List<Account> accountData) {
        mAccountData = accountData;
        notifyDataSetChanged();
    }
    void setAccountData(Account account) {
        for(int i = 0; i < mAccountData.size(); i++) {
            if(mAccountData.get(i).getType().equals(account.getType())) {
                mAccountData.set(i, account);
            }
        }
        notifyDataSetChanged();
    }

    class AccountAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mAccountTypeView;
        final LinearLayout mAccountIdentifierContainer;
        final LinearLayout mAccountBalanceContainer;


        AccountAdapterViewHolder(View itemView) {
            super(itemView);
            mAccountTypeView = itemView.findViewById(R.id.account_type);
            mAccountIdentifierContainer = itemView.findViewById(R.id.account_identifier_container);
            mAccountBalanceContainer = itemView.findViewById(R.id.account_balance_container);
            itemView.setOnClickListener(this);
        }

        void bind(Account account) {
            mAccountTypeView.setText(account.getType());
            mAccountIdentifierContainer.removeAllViews();
            mAccountBalanceContainer.removeAllViews();

            for(Account.Identifier identifier : account.getIdentifiers()) {
                View view = mLayoutInflater.inflate(R.layout.account_identifier, null);

                TextView mIdentifierNameView = view.findViewById(R.id.account_identifier_name);
                mIdentifierNameView.setText(identifier.getName());

                TextView mIdentifierValueView = view.findViewById(R.id.account_identifier_value);
                mIdentifierValueView.setText(identifier.getValue());

                mAccountIdentifierContainer.addView(view);
            }

            for(Account.Balance balance : account.getBalances()) {
                View view = mLayoutInflater.inflate(R.layout.account_balance, null);

                TextView mIdentifierNameView = view.findViewById(R.id.balance_value);
                mIdentifierNameView.setText(balance.getAmount().toString());

                TextView mIdentifierValueView = view.findViewById(R.id.balance_currency);
                mIdentifierValueView.setText(balance.getCurrency());

                mAccountBalanceContainer.addView(view);
            }
        }

        @Override
        public void onClick(View v) {
            Account currentAccount = mAccountData.get(getAdapterPosition());
            mAccountClickListener.onAccountClick(currentAccount.getType(), currentAccount.getIdentifiers().get(0).getValue());

        }
    }
}
