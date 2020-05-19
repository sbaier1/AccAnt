package de.mordsgau.accant.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.mordsgau.accant.ui.CreateReceiptActivity;

@Module
public abstract class CreateReceiptActivityModule {

    @ContributesAndroidInjector(modules = {})
    abstract CreateReceiptActivity createReceiptActivityInjector();
}