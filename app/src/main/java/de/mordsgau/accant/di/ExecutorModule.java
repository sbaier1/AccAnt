package de.mordsgau.accant.di;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ExecutorModule {

    private final ExecutorService executorService;


    public ExecutorModule() {
        this.executorService = Executors.newFixedThreadPool(3);
    }

    @Singleton
    @Provides
    ExecutorService providesExecutorService() {
        return executorService;
    }
}
