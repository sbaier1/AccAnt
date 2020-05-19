package de.mordsgau.accant.model;

import java.util.List;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.mordsgau.accant.model.atomic.AccAntLoginInformation;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.banking.Transaction;
import de.mordsgau.accant.model.metadata.Budget;

/**
 * Describes how to implement a backend for AccAnt data
 * <p>
 * All information presumes being tied to a user by optionally providing {@link AccAntLoginInformation}
 */
public interface IAccAntData {

    /**
     * Initialize the storage, executor if necessary (e.g. database connection)
     */
    void start(@Nullable final AccAntLoginInformation loginData);

    /**
     * De initialize the storage backend, executor if necessary
     */
    void stop();


    /**
     * Add an account to the storage backend
     *
     * @param account account to add
     * @return future waiting to return
     */
    Future<Void> addAccount(@NonNull final Account account);

    /**
     * Add a receipt to the storage backend
     *
     * @param receipt receipt to add
     * @return future waiting to return
     */
    Future<Void> addReceipt(@NonNull final Receipt receipt);

    /**
     * Add a transaction to the storage backend
     *
     * @param transaction transaction to add
     * @return future waiting to return
     */
    Future<Void> addTransaction(@NonNull final Transaction transaction);

    /**
     * Add a budget to the storage backend
     *
     * @param budget budget to add
     * @return future waiting to return
     */
    Future<Void> addBudget(@NonNull final Budget budget);

    /**
     * Asynchronously get all (cached) account information stored in the backend
     *
     * @return Future waiting for the list of accounts or possible errors
     */
    Future<List<Account>> getAccounts();

    /**
     * Asynchronously get all (cached) stored transactions for a given account
     *
     * @param account account to get the transactions for
     * @return Future containing transactions for the account or empty list
     */
    Future<List<Transaction>> getTransactions(@NonNull final Account account);

    /**
     * Get all receipts stored in the backend
     *
     * @return list of receipts
     */
    Future<List<Receipt>> getReceipts();

    /**
     * Get all budgets stored in the backend
     *
     * @return list of budgets
     */
    Future<List<Receipt>> getBudgets();

    Future<Void> updateReceipt(@NonNull final Receipt original, @NonNull final Receipt newReceipt);

    Future<Void> updateBudget(@NonNull final Budget original, @NonNull final Budget newBudget);

    Future<Void> updateAccount(@NonNull final Account original, @NonNull final Account newAccount);

    Future<Void> updateTransaction(@NonNull final Transaction original, @NonNull final Transaction newTransaction);

    Future<Void> deleteReceipt(@NonNull final Receipt receipt);

    Future<Void> deleteBudget(@NonNull final Budget budget);

    Future<Void> deleteAccount(@NonNull final Account account);

    Future<Void> deleteTransaction(@NonNull final Transaction transaction);


}
