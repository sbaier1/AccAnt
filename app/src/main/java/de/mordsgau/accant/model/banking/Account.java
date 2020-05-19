package de.mordsgau.accant.model.banking;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import de.mordsgau.accant.model.atomic.RowData;

/**
 * Holds information about a bank account
 * <p>
 * TODO login info oder so
 */
@Entity
public class Account extends RowData {
    @PrimaryKey(autoGenerate = true)
    private int id;


    //FIXME: list of transactions as relation

    private String mName;
    private float mAmount;
    private String mLastFourDigits;
    private int mColor;

    @Ignore
    private DecimalFormat mFormatter;

    public Account(String name, float amount, String lastFourDigits, int color) {
        mName = name;
        mAmount = amount;
        mLastFourDigits = lastFourDigits;
        mColor = color;

        mFormatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = mFormatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        mFormatter.setDecimalFormatSymbols(symbols);
    }

    @Override
    public String getRowName() {
        return mName;
    }

    @Override
    public String getRowSecondaryString() {
        return mLastFourDigits;
    }

    @Override
    public float getRowAmount() {
        return mAmount;
    }

    @Override
    public float getRowLimitAmount() {
        return mAmount;
    }

    @Override
    public String getRowAmountString() {
        return mFormatter.format(mAmount);
    }

    @Override
    public int getRowColor() {
        return mColor;
    }

    @Override
    public boolean showSecondaryStringObfuscation() {
        return true;
    }

    @Override
    public boolean showAccentBar() {
        return true;
    }

    @Override
    public boolean showFractionBar() {
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMName() {
        return mName;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }

    public float getMAmount() {
        return mAmount;
    }

    public void setMAmount(float mAmount) {
        this.mAmount = mAmount;
    }

    public String getMLastFourDigits() {
        return mLastFourDigits;
    }

    public void setMLastFourDigits(String mLastFourDigits) {
        this.mLastFourDigits = mLastFourDigits;
    }

    public int getMColor() {
        return mColor;
    }

    public void setMColor(int mColor) {
        this.mColor = mColor;
    }

    public DecimalFormat getmFormatter() {
        return mFormatter;
    }

    public void setmFormatter(DecimalFormat mFormatter) {
        this.mFormatter = mFormatter;
    }
}
