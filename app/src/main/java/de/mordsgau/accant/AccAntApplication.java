package de.mordsgau.accant;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import de.mordsgau.accant.di.AppComponent;
import de.mordsgau.accant.di.DaggerAppComponent;
import de.mordsgau.accant.di.ExecutorModule;
import de.mordsgau.accant.di.RoomModule;

public class AccAntApplication extends Application implements HasActivityInjector {

    public static AccAntApplication singleton;

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        final AppComponent build = DaggerAppComponent.builder()
                .roomModule(new RoomModule(this))
                .executorModule(new ExecutorModule())
                .build();
        build.inject(this);
    }




    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    public static Context appContext() {
        return singleton.getApplicationContext();
    }
}
