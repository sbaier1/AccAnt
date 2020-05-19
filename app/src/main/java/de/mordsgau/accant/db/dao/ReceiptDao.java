package de.mordsgau.accant.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.model.ReceiptWithoutItems;
import de.mordsgau.accant.model.atomic.ReceiptItem;
import de.mordsgau.accant.model.atomic.ReceiptItemTag;
import de.mordsgau.accant.model.atomic.ReceiptItemWithoutTags;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class ReceiptDao {

    public int save(Receipt receipt) {
        final int id = (int) insertReceipt(receipt.getReceipt());
        final List<ReceiptItem> items = receipt.getItems();
        for (ReceiptItem item : items) {
            item.setReceiptId(id);
            final long itemKey = insertReceiptItem(item.getReceiptItem());
            final List<ReceiptItemTag> tags = item.getTags();
            for (ReceiptItemTag tag : tags) {
                tag.setItemId(itemKey);
                insertReceiptItemTag(tag);
            }
            //FIXME add tags
        }
        return id;
    }

    @Insert(onConflict = REPLACE)
    public abstract long insertReceiptItem(ReceiptItemWithoutTags item);

    @Insert(onConflict = REPLACE)
    public abstract void insertReceiptItemTag(ReceiptItemTag itemTag);

    @Insert(onConflict = REPLACE)
    public abstract long insertReceipt(ReceiptWithoutItems receipt);

    @Delete
    public abstract void deleteReceiptItem(ReceiptItemWithoutTags item);

    @Delete
    public abstract void deleteReceiptItem(List<ReceiptItemWithoutTags> items);

    @Delete
    public abstract void deleteReceipt(ReceiptWithoutItems receipt);

    @Delete
    public abstract void deleteTag(ReceiptItemTag tag);

    //FIXME convenience method to delete entire receipt / receiptItem

    @Query("SELECT id FROM ReceiptWithoutItems")
    public abstract LiveData<List<Integer>> receiptIds();

    @Query("SELECT * FROM ReceiptWithoutItems")
    public abstract LiveData<List<Receipt>> allReceipts();

    @Query("SELECT * FROM ReceiptWithoutItems WHERE id = :receiptId LIMIT 1")
    public abstract LiveData<Receipt> load(int receiptId);

    //@Query("SELECT * FROM ReceiptItemTag WHERE id = " + UNASSIGNED_TAG)
    @Query("SELECT * FROM ReceiptItemTag GROUP BY tagName")
    public abstract LiveData<List<ReceiptItemTag>> getUnassignedTags();

    @Query("SELECT color FROM ReceiptItemTag WHERE tagName = :tagName")
    public abstract int getColorForTag(String tagName);


    @Update
    public abstract void updateItemName(ReceiptItemWithoutTags withoutTags);

    @Update
    public abstract void updateItemPrice(ReceiptItemWithoutTags receiptItem);

    @Update
    public abstract void updateReceipt(ReceiptWithoutItems newReceipt);

}
