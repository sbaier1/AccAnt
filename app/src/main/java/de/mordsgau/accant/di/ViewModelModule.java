package de.mordsgau.accant.di;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import de.mordsgau.accant.ui.viewmodel.ReceiptModel;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReceiptModel.class)
    abstract ViewModel bindReceiptViewModel(ReceiptModel receiptViewModel);
}
