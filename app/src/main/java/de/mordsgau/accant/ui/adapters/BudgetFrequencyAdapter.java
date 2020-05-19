package de.mordsgau.accant.ui.adapters;


import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.ArrayList;

import de.mordsgau.accant.R;
import de.mordsgau.accant.model.metadata.BudgetFrequency;

public class BudgetFrequencyAdapter implements SpinnerAdapter {
    private LayoutInflater inflater;

    public BudgetFrequencyAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    final ArrayList<BudgetFrequency> budgetFrequencies = Lists.newArrayList(
            BudgetFrequency.WEEKLY,
            BudgetFrequency.MONTHLY,
            BudgetFrequency.YEARLY
    );

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.budget_frequency_spinner_item, parent, false);
        }
        TextView text = view.findViewById(R.id.account_spinner_account_name_short);
        text.setText(budgetFrequencies.get(position).readableString());
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
        return BudgetFrequency.values().length;
    }

    @Override
    public Object getItem(int position) {
        if (position < getCount()) {
            return budgetFrequencies.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item_short, parent, false);
        }
        TextView text = view.findViewById(R.id.account_spinner_account_name_short);
        text.setText(budgetFrequencies.get(position).readableString());
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}

