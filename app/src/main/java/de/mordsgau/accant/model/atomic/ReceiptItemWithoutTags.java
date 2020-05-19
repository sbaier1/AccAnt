package de.mordsgau.accant.model.atomic;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ReceiptItemWithoutTags {


    @PrimaryKey(autoGenerate = true)
    @NonNull
    protected int id;

    protected int receiptId;

    protected String itemName;

    protected Double itemPrice;

    public ReceiptItemWithoutTags(int id, int receiptId, String itemName, Double itemPrice) {
        this.id = id;
        this.receiptId = receiptId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    public ReceiptItemWithoutTags() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
