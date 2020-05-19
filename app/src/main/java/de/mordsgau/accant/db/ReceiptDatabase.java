package de.mordsgau.accant.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import de.mordsgau.accant.db.dao.AccountDao;
import de.mordsgau.accant.db.dao.ReceiptDao;
import de.mordsgau.accant.model.ReceiptWithoutItems;
import de.mordsgau.accant.model.atomic.ReceiptItemTag;
import de.mordsgau.accant.model.atomic.ReceiptItemWithoutTags;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.metadata.Budget;

@Database(entities = {ReceiptWithoutItems.class, ReceiptItemWithoutTags.class, ReceiptItemTag.class, Account.class, Budget.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class ReceiptDatabase extends RoomDatabase {
    public abstract ReceiptDao receiptDao();

    public abstract AccountDao accountDao();
}