package de.mordsgau.accant.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.banking.Transaction;

/**
 * Describes AccAnt interaction with a banking API
 */
public interface IBankingConnection {

    /**
     * Logon to banking API.
     *
     * @param loginData provide a map containing necessary information specific to the banking API
     */
    void login(final @NonNull Map<String, String> loginData);

    /**
     * Terminate connection to the bank API.
     */
    void logout();


    /**
     * Get list of bank accounts accessible via the banking connection
     *
     * @return list of accounts
     */
    Future<List<Account>> getAccounts();

    /**
     * Asynchronously get all stored transactions for a given account
     *
     * @param account account to get the transactions for
     * @return Future containing transactions for the account or empty list
     */
    Future<List<Transaction>> getTransactions(@NonNull final Account account);


    /**
     * Asynchronously get all stored transactions for a given account and start date.
     *
     * @param account account to get the transactions for
     * @param startDate point in time from which to collect the transactions
     * @param endDate end of time frame until which to collect the transactions
     * @return Future containing transactions for the account or empty list
     */
    Future<List<Transaction>> getTransactions(@NonNull final Account account, Date startDate, Date endDate);
}
