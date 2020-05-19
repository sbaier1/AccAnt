package de.mordsgau.accant.model.banking;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Describes a bank transaction
 */
@Entity
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int accountId;

    private Date date;

    private Double amount;

    private String title;
    private String subTitle;

    private String source;
    private String destination;

    public Transaction(Date date, Double amount, String source, String destination, String title, String subTitle) {
        this.date = date;
        this.amount = amount;
        this.source = source;
        this.destination = destination;
        this.title = title;
        this.subTitle = subTitle;
    }

    public Date getDate() {
        return date;
    }

    public Double getAmount() {
        return amount;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
