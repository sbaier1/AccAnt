package de.mordsgau.accant.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.metadata.Budget;

@Dao
public interface AccountDao {

    @Insert
    long insertAccount(Account account);

    @Insert
    long insertBudget(Budget budget);

    @Delete
    void deleteAccount(Account account);

    @Delete
    void deleteBudget(Budget budget);

    @Query("SELECT * FROM Account WHERE id = :accountId")
    LiveData<Account> getAccount(long accountId);

    @Query("SELECT * FROM Account")
    LiveData<List<Account>> allAcounts();

    @Query("SELECT * FROM Budget")
    LiveData<List<Budget>> allBudgets();

    @Query("SELECT * FROM Budget WHERE id = :budgetId")
    LiveData<Budget> getBudget(long budgetId);


    @Query("SELECT * FROM Budget WHERE frequency = 0")
    LiveData<List<Budget>> weeklyBudgets();

    @Query("SELECT * FROM Budget WHERE frequency = 1")
    LiveData<List<Budget>> monthlyBudgets();

    @Query("SELECT * FROM Budget WHERE frequency = 2")
    LiveData<List<Budget>> yearlyBudgets();

    @Query("SELECT * FROM Budget WHERE linkedItemId = :id")
    LiveData<List<Budget>> getBudgetsForAccount(int id);

    @Update
    void updateBudget(Budget selectedBudget);

    @Update
    void updateAccount(Account selectedAccount);
}
