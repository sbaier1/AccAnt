package de.mordsgau.accant.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.mordsgau.accant.R;
import de.mordsgau.accant.model.metadata.Budget;
import de.mordsgau.accant.ui.view.FractionBarView;

public class BudgetFragmentRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();

    private List<Budget> budgets;

    private int darkColor;

    public BudgetFragmentRowAdapter() {
        currencyInstance.setCurrency(Currency.getInstance(Locale.getDefault()));
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        darkColor = ContextCompat.getColor(context, R.color.backgroundDark);
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View rootLayout = inflater.inflate(R.layout.layout_budget_row, parent, false);
        return new BudgetRowViewHolder(rootLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BudgetRowViewHolder viewHolder = (BudgetRowViewHolder) holder;
        final Budget budget = this.budgets.get(position);
        final float currentAmount = budget.getMCurrAmount();
        final float maxAmount = budget.getMLimitAmount();
        viewHolder.budgetName.setText(budget.getMName());
        viewHolder.budgetCurrent.setText(currencyInstance.format(currentAmount));
        viewHolder.budgetTotal.setText(currencyInstance.format(maxAmount));
        final float fraction = currentAmount / maxAmount;
        int[] colors = new int[]{budget.getMColor(), darkColor};
        float[] fractions = new float[]{fraction, 1 - fraction};
        viewHolder.barView.setData(colors, fractions);
    }

    @Override
    public int getItemCount() {
        return budgets == null ? 0 : budgets.size();
    }

    public class BudgetRowViewHolder extends RecyclerView.ViewHolder {

        public TextView budgetName;
        public TextView budgetCurrent;
        public TextView budgetTotal;
        public FractionBarView barView;
        public ImageView arrow;

        public BudgetRowViewHolder(View itemView) {
            super(itemView);
            budgetName = itemView.findViewById(R.id.budget_name);
            budgetCurrent = itemView.findViewById(R.id.budget_current_value);
            budgetTotal = itemView.findViewById(R.id.budget_max_value);
            barView = itemView.findViewById(R.id.budget_row_bar_chart);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }
}
