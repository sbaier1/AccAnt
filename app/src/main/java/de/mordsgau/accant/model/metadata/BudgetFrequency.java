package de.mordsgau.accant.model.metadata;

import de.mordsgau.accant.AccAntApplication;
import de.mordsgau.accant.R;

/**
 * How often a budget is rotated
 */
public enum BudgetFrequency {
    WEEKLY(R.string.budget_frequency_weekly),
    MONTHLY(R.string.budget_frequency_monthly),
    YEARLY(R.string.budget_frequency_yearly);

    private int resId;

    BudgetFrequency(int resId) {
        this.resId = resId;
    }

    public String readableString() {
        return AccAntApplication.appContext().getResources().getString(resId);
    }
}
