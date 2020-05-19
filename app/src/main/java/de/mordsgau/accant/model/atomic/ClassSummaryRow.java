package de.mordsgau.accant.model.atomic;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * Represents a row of data in a summary view
 * <p>
 * A row consists of a title, a possible subtitle below it, the percentage and a color associated with the class
 */
public class ClassSummaryRow {

    private final String rowTitle;

    @Nullable
    private final String rowSubtitle;

    /* Percentage */
    private final float relativeAmount;

    /* Total money spent for this class */
    private final float absoluteAmount;

    private final int color;
    private final NumberFormat absoluteFormatter;

    /* If the class has a parent */
    private final @Nullable String parentClass;

    public ClassSummaryRow(final String className, final @Nullable String rowSubtitle, final float relativeAmount, final float absoluteAmount, final int color, final @Nullable String parentClass) {
        this.rowTitle = className;
        this.rowSubtitle = rowSubtitle;
        this.relativeAmount = relativeAmount;
        this.absoluteAmount = absoluteAmount;
        this.color = color;
        this.absoluteFormatter = NumberFormat.getCurrencyInstance();
        this.parentClass = parentClass;
    }

    public String getRowTitle() {
        return rowTitle;
    }

    @Nullable
    public String getRowSubtitle() {
        return rowSubtitle;
    }

    public float getRelativeAmount() {
        return relativeAmount;
    }

    public int getColor() {
        return color;
    }

    public float getAbsoluteAmount() {
        return absoluteAmount;
    }

    public String getAbsoluteAmountString() {
        // We remove the symbol here, it will be displayed as a prefix in the future TODO
        return absoluteFormatter.format(absoluteAmount)
                .replace(absoluteFormatter.getCurrency().getSymbol(), "")
                .replace(" ", "");
    }

    public String getParentClass() {
        return parentClass;
    }

    public String getRelativeAmountString() {
        return String.format(Locale.getDefault(), "%.02f", relativeAmount);
    }
}
