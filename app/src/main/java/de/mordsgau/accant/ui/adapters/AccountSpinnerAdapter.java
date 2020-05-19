package de.mordsgau.accant.ui.adapters;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import de.mordsgau.accant.R;
import de.mordsgau.accant.model.banking.Account;

public class AccountSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final LayoutInflater inflater;

    public AccountSpinnerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public static final int ADD_ACCOUNT_ID = -1;
    List<Account> accountList;

    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            return false;
        }
        return true;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (position == 0) {
            // No selection item
            return getInitialSelectionView(parent, true);
        }
        if (!(view instanceof RelativeLayout)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_spinner_item_dropdown, parent, false);
        }
        TextView accountName = view.findViewById(R.id.account_spinner_account_name);
        TextView accountBalance = view.findViewById(R.id.account_spinner_balance);
        if (accountList != null && position < accountList.size() + 1) {
            accountName.setText(accountList.get(position - 1).getMName());
            accountBalance.setText("123 €");
        } else {
            accountName.setText(R.string.add_account_string);
            accountBalance.setText("");

        }
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        // There is always an add account view
        return accountList == null ? 2 : accountList.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        if (accountList != null && position < accountList.size() && position >= 0) {
            return accountList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (accountList != null && position <= accountList.size() && position > 0) {
            return accountList.get(position - 1).getId();
        }
        if (position == 0) {
            return NO_SELECTION;
        } else if ((accountList != null && position == accountList.size() + 1) || (accountList == null && position == 1)) {
            return ADD_ACCOUNT_ID;
        } else {
            return NO_SELECTION;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (position == 0 || (accountList != null && accountList.size() == 0)) {
            // No selection item
            return getInitialSelectionView(parent, false);
        }
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_short, parent, false);
        }
        TextView accountName = view.findViewById(R.id.account_spinner_account_name_short);
        if (accountList != null && position < accountList.size() + 1) {
            // TODO use actual account names from the list here
            if (accountName != null) {
                accountName.setText(accountList.get(position - 1).getMName());
            }
        } else {
            return getInitialSelectionView(parent, false);
        }
        return view;
    }

    private View getInitialSelectionView(ViewGroup parent, boolean dropdown) {
        final View inflated = inflater.inflate(R.layout.spinner_item_short, parent, false);
        final TextView textView = inflated.findViewById(R.id.account_spinner_account_name_short);
        textView.setText(R.string.no_account_selection);
        if (dropdown) {
            textView.setHeight(0);
        }
        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        // Types: 0 = account, 1 = add account item, 2 = no selection
        if (accountList != null && position < accountList.size()) {
            return 0;
        }
        if (position == 0) {
            return 2;
        } else if (position == 1 || (accountList != null && position == accountList.size())) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public void setAccounts(List<Account> accounts) {
        this.accountList = accounts;
    }
}