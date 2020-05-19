package de.mordsgau.accant.model.metadata;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import de.mordsgau.accant.model.atomic.RowData;

/**
 * Describes a budget
 *
 * TODO
 */
@Entity
public class Budget  extends RowData {
    @PrimaryKey(autoGenerate = true)
    private int id;

    /* We can theoretically link a budget to a receipt or a single tag over which to aggregate spendings
     * Therefore this name is ambiguous. This relation needs to be properly defined first */
    private int linkedItemId;

    private String mName;
    private float mCurrAmount;
    private float mLimitAmount;
    private int mColor;

    /* These are initialized in the constructor and not to be serialized to DB
     * TODO: are these needed at all?  */
    @Ignore
    private DecimalFormat mFormatter;
    @Ignore
    private NumberFormat mCurrencyFormatter;

    private String currency;

    private BudgetFrequency frequency;

    public Budget() {
    }

    public Budget(String name, float currAmount, float limitAmount, int color, String currency, BudgetFrequency frequency) {
        mName = name;
        mCurrAmount = currAmount;
        mLimitAmount = limitAmount;
        mColor = color;
        this.currency = currency;
        this.frequency = frequency;

        mFormatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = mFormatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        mFormatter.setDecimalFormatSymbols(symbols);
        mCurrencyFormatter = NumberFormat.getCurrencyInstance();
    }

    @Override
    public String getRowName() {
        return mName;
    }

    @Override
    public String getRowSecondaryString() {
        String limitString = mCurrencyFormatter.format(Math.round(mLimitAmount));
        String[] limitDecimalSplit = limitString.split("\\.");
        return mCurrencyFormatter.format(mCurrAmount) + " / " + limitDecimalSplit[0];
    }

    @Override
    public float getRowAmount() {
        return mCurrAmount;
    }

    @Override
    public float getRowLimitAmount() {
        return mLimitAmount;
    }

    @Override
    public String getRowAmountString() {
        // Show the remaining budget where other data types show amount
        return mFormatter.format(mLimitAmount - mCurrAmount);
    }

    @Override
    public int getRowColor() {
        return mColor;
    }

    @Override
    public boolean showSecondaryStringObfuscation() {
        return false;
    }

    @Override
    public boolean showAccentBar() {
        return false;
    }

    @Override
    public boolean showFractionBar() {
        return true;
    }

    @Override
    public String getAmountQualifierString() {
        return "Left";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLinkedItemId() {
        return linkedItemId;
    }

    public void setLinkedItemId(int linkedItemId) {
        this.linkedItemId = linkedItemId;
    }

    public String getMName() {
        return mName;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }

    public float getMCurrAmount() {
        return mCurrAmount;
    }

    public void setMCurrAmount(float mCurrAmount) {
        this.mCurrAmount = mCurrAmount;
    }

    public float getMLimitAmount() {
        return mLimitAmount;
    }

    public void setMLimitAmount(float mLimitAmount) {
        this.mLimitAmount = mLimitAmount;
    }

    public int getMColor() {
        return mColor;
    }

    public void setMColor(int mColor) {
        this.mColor = mColor;
    }

    public DecimalFormat getMFormatter() {
        return mFormatter;
    }

    public void setMFormatter(DecimalFormat mFormatter) {
        this.mFormatter = mFormatter;
    }

    public NumberFormat getMCurrencyFormatter() {
        return mCurrencyFormatter;
    }

    public void setMCurrencyFormatter(NumberFormat mCurrencyFormatter) {
        this.mCurrencyFormatter = mCurrencyFormatter;
    }

    public String getCurrency() {
        return currency;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public float getmCurrAmount() {
        return mCurrAmount;
    }

    public void setmCurrAmount(float mCurrAmount) {
        this.mCurrAmount = mCurrAmount;
    }

    public float getmLimitAmount() {
        return mLimitAmount;
    }

    public void setmLimitAmount(float mLimitAmount) {
        this.mLimitAmount = mLimitAmount;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public DecimalFormat getmFormatter() {
        return mFormatter;
    }

    public void setmFormatter(DecimalFormat mFormatter) {
        this.mFormatter = mFormatter;
    }

    public NumberFormat getmCurrencyFormatter() {
        return mCurrencyFormatter;
    }

    public void setmCurrencyFormatter(NumberFormat mCurrencyFormatter) {
        this.mCurrencyFormatter = mCurrencyFormatter;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BudgetFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(BudgetFrequency frequency) {
        this.frequency = frequency;
    }
}
