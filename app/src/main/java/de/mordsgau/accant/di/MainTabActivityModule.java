package de.mordsgau.accant.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.mordsgau.accant.fragments.BalanceFragment;
import de.mordsgau.accant.fragments.BillFragment;
import de.mordsgau.accant.fragments.BudgetFragment;
import de.mordsgau.accant.fragments.OverviewFragment;

@Module
public abstract class MainTabActivityModule {

    @ContributesAndroidInjector(modules = BillFragmentModule.class)
    abstract BillFragment billFragmentInjector();

    @ContributesAndroidInjector(modules = BalanceFragmentModule.class)
    abstract BalanceFragment balanceFragmentInjector();

    @ContributesAndroidInjector(modules = BudgetFragmentModule.class)
    abstract BudgetFragment budgetFragmentInjector();

    @ContributesAndroidInjector(modules = CardFragmentModule.class)
    abstract OverviewFragment cardFragmentInjector();
}