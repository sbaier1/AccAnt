package de.mordsgau.accant.db;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import de.mordsgau.accant.db.dao.AccountDao;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.metadata.Budget;

@Singleton
public class AccountRepository {

    private final AccountDao accountDao;

    private final ExecutorService executorService;

    public AccountRepository(AccountDao accountDao, ExecutorService executorService) {
        this.accountDao = accountDao;
        this.executorService = executorService;
    }

    public LiveData<List<Account>> getAccounts() {
        return accountDao.allAcounts();
    }

    public long addAccount(Account account) {
        try {
            return executorService.submit(() -> accountDao.insertAccount(account)).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public LiveData<List<Budget>> getBudgets() {
        return accountDao.allBudgets();
    }

    public LiveData<List<Budget>> getBudgetsWeekly() {
        return accountDao.weeklyBudgets();
    }

    public LiveData<List<Budget>> getBudgetsMonthly() {
        return accountDao.monthlyBudgets();
    }

    public LiveData<List<Budget>> getBudgetsYearly() {
        return accountDao.yearlyBudgets();
    }

    public long addBudget(Budget budget) throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.submit(() -> accountDao.insertBudget(budget)).get(10, TimeUnit.SECONDS);
    }

    public LiveData<List<Budget>> getBudgetsForAccount(int id) {
        return accountDao.getBudgetsForAccount(id);
    }

    public void updateBudget(Budget selectedBudget) {
        executorService.submit(() -> {
            accountDao.updateBudget(selectedBudget);
        });
    }

    public void updateAccount(Account account) {
        executorService.submit(() -> {
            accountDao.updateAccount(account);
        });
    }
}
