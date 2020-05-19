package de.mordsgau.accant.model.atomic;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ReceiptItemTag implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    private long itemId;

    private int color;

    private String tagName;

    private Double confidence;

    public ReceiptItemTag() {
    }

    public ReceiptItemTag(int color, String tagName, Double confidence) {
        this.color = color;
        this.tagName = tagName;
        this.confidence = confidence;
    }

    protected ReceiptItemTag(Parcel in) {
        id = in.readInt();
        itemId = in.readLong();
        color = in.readInt();
        tagName = in.readString();
        if (in.readByte() == 0) {
            confidence = null;
        } else {
            confidence = in.readDouble();
        }
    }

    public static final Creator<ReceiptItemTag> CREATOR = new Creator<ReceiptItemTag>() {
        @Override
        public ReceiptItemTag createFromParcel(Parcel in) {
            return new ReceiptItemTag(in);
        }

        @Override
        public ReceiptItemTag[] newArray(int size) {
            return new ReceiptItemTag[size];
        }
    };

    public int getColor() {
        return color;
    }

    public long getItemId() {
        return itemId;
    }

    public String getTagName() {
        return tagName;
    }

    public Double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return tagName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(itemId);
        dest.writeInt(color);
        dest.writeString(tagName);
        dest.writeDouble(confidence);
    }
}
