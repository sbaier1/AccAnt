package de.mordsgau.accant.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;
import de.mordsgau.accant.model.atomic.ReceiptItem;
import de.mordsgau.accant.model.atomic.ReceiptItemWithoutTags;

/**
 * Models the information contained in a receipt generally
 */
public class Receipt implements Parcelable {

    protected Receipt(Parcel in) {
        items = in.createTypedArrayList(ReceiptItem.CREATOR);
    }

    public static final Creator<Receipt> CREATOR = new Creator<Receipt>() {
        @Override
        public Receipt createFromParcel(Parcel in) {
            return new Receipt(in);
        }

        @Override
        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };

    public void setItems(List<ReceiptItem> items) {
        this.items = items;
    }

    public void setReceipt(ReceiptWithoutItems receipt) {
        this.receipt = receipt;
    }

    @Relation(parentColumn = "id", entityColumn = "receiptId", entity = ReceiptItemWithoutTags.class)
    private List<ReceiptItem> items;

    @Embedded
    ReceiptWithoutItems receipt;


    public Receipt() {
    }

    public Receipt(@NonNull Double sum, @NonNull List<ReceiptItem> items,
                   @Nullable Date date, @Nullable Map<String, String> metadata) {
        receipt = new ReceiptWithoutItems();
        receipt.sum = sum;
        this.items = items;
        receipt.date = date;
        receipt.metadata = metadata;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "[receiptId: " + receipt.id + ", items: " + items.toString() + "sum: " + receipt.sum + " date: " + receipt.date +", account "+receipt.accountId+", budget "+receipt.budgetId+ "]";
    }


    public int getId() {
        return receipt.id;
    }

    public Double getSum() {
        return receipt.sum;
    }

    public Date getDate() {
        return receipt.date;
    }

    public Map<String, String> getMetadata() {
        return receipt.metadata;
    }

    public ReceiptWithoutItems getReceipt() {
        return receipt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(receipt.id);
        dest.writeDouble(receipt.sum);
        dest.writeSerializable(receipt.date);
        dest.writeMap(receipt.metadata);
        dest.writeList(items);
    }
}
