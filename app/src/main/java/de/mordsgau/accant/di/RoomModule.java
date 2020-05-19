package de.mordsgau.accant.di;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import de.mordsgau.accant.db.AccountRepository;
import de.mordsgau.accant.db.ReceiptDatabase;
import de.mordsgau.accant.db.ReceiptRepository;
import de.mordsgau.accant.db.dao.AccountDao;
import de.mordsgau.accant.db.dao.ReceiptDao;

@Module
public class RoomModule {
    public static final String DATABASE_NAME = "receipt-db";

    private final ReceiptRepository receiptRepository;
    private ReceiptDatabase receiptDatabase;
    private AccountRepository accountRepository;

    public RoomModule(Application mApplication) {
        receiptDatabase = Room.databaseBuilder(mApplication, ReceiptDatabase.class, DATABASE_NAME).build();
        final ExecutorService dbExecutor = Executors.newFixedThreadPool(2);
        receiptRepository = new ReceiptRepository(receiptDatabase.receiptDao(), dbExecutor);
        accountRepository = new AccountRepository(receiptDatabase.accountDao(), dbExecutor);
    }

    @Singleton
    @Provides
    ReceiptDatabase receiptDatabase() {
        return receiptDatabase;
    }

    @Singleton
    @Provides
    ReceiptDao receiptDao() {
        return receiptDatabase.receiptDao();
    }

    @Singleton
    @Provides
    AccountDao accountDao() {
        return receiptDatabase.accountDao();
    }

    @Singleton
    @Provides
    ReceiptRepository receiptRepository() {
        return receiptRepository;
    }

    @Singleton
    @Provides
    AccountRepository accountRepository() {
        return accountRepository;
    }
}