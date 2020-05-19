package de.mordsgau.accant.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.mordsgau.accant.MainTabActivity;
import de.mordsgau.accant.ui.CameraCaptureActivity;
import de.mordsgau.accant.ui.CreateReceiptActivity;

@Module
abstract class AppModule {

    @ContributesAndroidInjector(modules = MainTabActivityModule.class)
    abstract MainTabActivity mainActivityInjector();

    @ContributesAndroidInjector(modules = CreateReceiptActivityModule.class)
    abstract CreateReceiptActivity createReceiptActivityInjector();

    @ContributesAndroidInjector(modules = CameraCaptureActivityModule.class)
    abstract CameraCaptureActivity createCameraCaptureActivityInjector();
}
