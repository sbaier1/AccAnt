package de.mordsgau.accant.ui.adapters;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;
import de.mordsgau.accant.R;
import de.mordsgau.accant.model.metadata.Budget;
import de.mordsgau.accant.ui.view.FractionBarView;

public class BudgetSpinnerAdapter implements SpinnerAdapter {


    public static final int ADD_BUDGET_ID = -1;
    List<Budget> budgetList;
    private final LayoutInflater inflater;

    public BudgetSpinnerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (position == 0) {
            // No selection item
            return getInitialSelectionView(parent, true);
        }
        if (!(view instanceof ConstraintLayout)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_spinner_item_dropdown, parent, false);
        }
        TextView budgetName = view.findViewById(R.id.budget_spinner_budget_name);
        TextView budgetCurrent = view.findViewById(R.id.budget_spinner_current);
        TextView budgetSeparator = view.findViewById(R.id.budget_spinner_separator);
        TextView budgetLimit = view.findViewById(R.id.budget_spinner_total);
        FractionBarView fractionView = view.findViewById(R.id.budget_spinner_fraction_used);
        if (budgetList != null && position < budgetList.size() + 1) {
            final Budget budget = budgetList.get(position - 1);
            budgetName.setText(budget.getMName());
            // TODO replace all format calls with proper currency formatter, store currency properly in DB
            budgetCurrent.setText(String.format(Locale.getDefault(), "%.02f " + budget.getCurrency(), budget.getMCurrAmount()));
            budgetSeparator.setText("/");
            budgetLimit.setText(String.format(Locale.getDefault(), "%.02f " + budget.getCurrency(), budget.getmLimitAmount()));
            // Calculate fraction
            final float percentageUsed = budget.getMCurrAmount() / budget.getMLimitAmount();
            fractionView.setData(
                    new int[]{budget.getMColor(),
                            0xFF000000},
                    new float[]{percentageUsed,
                            1 - percentageUsed});
        } else {
            budgetName.setText(R.string.add_budget_string);
            // Hide balance
            budgetCurrent.setText("");
            budgetLimit.setText("");
            budgetSeparator.setText("");
            // Make fraction bar transparent
            fractionView.setData(new int[]{0}, new float[]{1F});
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
        return budgetList == null ? 2 : budgetList.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        if (budgetList != null && position < budgetList.size()) {
            return budgetList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (budgetList != null && position <= budgetList.size() && position > 0) {
            return budgetList.get(position - 1).getId();
        }
        if (position == 0) {
            return NO_SELECTION;
        } else if ((budgetList != null && position == budgetList.size() + 1) || (budgetList == null && position == 1)) {
            return ADD_BUDGET_ID;
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
        if (position == 0 || (budgetList != null && budgetList.size() == 0)) {
            // No selection item
            return getInitialSelectionView(parent, false);
        }
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_short, parent, false);
        }
        TextView accountName = view.findViewById(R.id.account_spinner_account_name_short);
        if (budgetList != null && position < budgetList.size() + 1) {
            // TODO use actual account names from the list here
            if (accountName != null) {
                accountName.setText(budgetList.get(position - 1).getMName());
            }
        } else {
            return getInitialSelectionView(parent, false);
        }
        return view;
    }

    private View getInitialSelectionView(ViewGroup parent, boolean dropdown) {
        final View inflated = inflater.inflate(R.layout.spinner_item_short, parent, false);
        final TextView textView = inflated.findViewById(R.id.account_spinner_account_name_short);
        textView.setText(R.string.no_budget_selection);
        if (dropdown) {
            textView.setHeight(0);
        }
        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        // Types: 0 = account, 1 = add budget item, 2 = no selection
        if (budgetList != null && position < budgetList.size()) {
            return 0;
        }
        if (position == 0) {
            return 2;
        } else if (position == 1 || (budgetList != null && position == budgetList.size())) {
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

    public void setBudgets(List<Budget> budgets) {
        this.budgetList = budgets;
    }
}

