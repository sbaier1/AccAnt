package de.mordsgau.accant.di;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import de.mordsgau.accant.AccAntApplication;
import de.mordsgau.accant.db.ReceiptDatabase;
import de.mordsgau.accant.db.ReceiptRepository;
import de.mordsgau.accant.db.dao.ReceiptDao;

@Singleton
@Component(dependencies = {}, modules = {AndroidSupportInjectionModule.class, AppModule.class, RoomModule.class, ViewModelModule.class, ExecutorModule.class})
public interface AppComponent {
    void inject(AccAntApplication app);

    ReceiptDatabase receiptDatabase();

    ReceiptDao receiptDao();

    ReceiptRepository receiptRepository();

}
