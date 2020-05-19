package de.mordsgau.accant.model.atomic;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

import static java.util.Collections.EMPTY_LIST;

public class ReceiptItem implements Parcelable {

    @Embedded
    ReceiptItemWithoutTags receiptItem;

    @Relation(parentColumn = "id", entityColumn = "itemId")
    private List<ReceiptItemTag> tags;

    public ReceiptItem() {
    }

    public ReceiptItem(String itemName, Double itemPrice, List<ReceiptItemTag> tags) {
        receiptItem = new ReceiptItemWithoutTags();
        receiptItem.itemName = itemName;
        receiptItem.itemPrice = itemPrice;
        this.tags = tags;
    }

    public ReceiptItem(String itemName, Double itemPrice) {
        receiptItem = new ReceiptItemWithoutTags();
        receiptItem.itemName = itemName;
        receiptItem.itemPrice = itemPrice;
        this.tags = EMPTY_LIST;
    }

    protected ReceiptItem(Parcel in) {
        tags = in.createTypedArrayList(ReceiptItemTag.CREATOR);
    }

    public static final Creator<ReceiptItem> CREATOR = new Creator<ReceiptItem>() {
        @Override
        public ReceiptItem createFromParcel(Parcel in) {
            return new ReceiptItem(in);
        }

        @Override
        public ReceiptItem[] newArray(int size) {
            return new ReceiptItem[size];
        }
    };

    public ReceiptItemWithoutTags getReceiptItem() {
        return receiptItem;
    }

    public void setReceiptItem(ReceiptItemWithoutTags receiptItem) {
        this.receiptItem = receiptItem;
    }

    public String getItemName() {
        return receiptItem.itemName;
    }

    public Double getItemPrice() {
        return receiptItem.itemPrice;
    }

    public List<ReceiptItemTag> getTags() {
        return tags;
    }

    public void setTags(List<ReceiptItemTag> tags) {
        this.tags = tags;
    }

    public void setItemName(String itemName) {
        receiptItem.itemName = itemName;
    }

    public void setItemPrice(Double itemPrice) {
        receiptItem.itemPrice = itemPrice;
    }

    @Override
    public String toString() {
        return "name: "+receiptItem.itemName+", price: "+receiptItem.itemPrice+", tags: "+tags;
    }

    public int getId() {
        return receiptItem.id;
    }

    public void setId(int id) {
        receiptItem.id = id;
    }

    public void setReceiptId(int id) {
        receiptItem.setReceiptId(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Actual item
        dest.writeInt(receiptItem.id);
        dest.writeInt(receiptItem.receiptId);
        dest.writeString(receiptItem.itemName);
        dest.writeDouble(receiptItem.itemPrice);
        // Tags
        dest.writeList(tags);
    }
}
