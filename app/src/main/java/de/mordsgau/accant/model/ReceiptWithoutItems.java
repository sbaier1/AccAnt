package de.mordsgau.accant.model;

import java.util.Date;
import java.util.Map;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ReceiptWithoutItems {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    protected int accountId;

    protected int budgetId;

    protected Double sum;


    /* Inferred time of purchase */
    protected Date date;

    /* Metadata such as vendor, payment method(?), ... */
    protected Map<String, String> metadata;

    public int getId() {
        return id;
    }

    public Double getSum() {
        return sum;
    }

    public Date getDate() {
        return date;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void setAccountId(int id) {
        this.accountId = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }
}

