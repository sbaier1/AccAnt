package de.mordsgau.accant.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import de.mordsgau.accant.R;
import de.mordsgau.accant.db.AccountRepository;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.metadata.Budget;
import de.mordsgau.accant.model.metadata.BudgetFrequency;
import de.mordsgau.accant.ui.adapters.AccountSpinnerAdapter;
import de.mordsgau.accant.ui.adapters.BudgetFrequencyAdapter;
import de.mordsgau.accant.ui.adapters.BudgetSpinnerAdapter;

import static android.widget.Adapter.NO_SELECTION;
import static de.mordsgau.accant.ui.adapters.AccountSpinnerAdapter.ADD_ACCOUNT_ID;

/**
 * This class collects static methods for building dialogues that appear in multiple views.
 */
public class DialogUtil {


    public static void buildAccountDialog(Context context, LayoutInflater inflater, AccountRepository accountRepository, ViewGroup parent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final View layout = inflater.inflate(R.layout.add_account_dialog, parent, false);
        // Input data
        EditText accountName = layout.findViewById(R.id.add_account_name);
        EditText userId = layout.findViewById(R.id.add_account_user_id);
        EditText bankCode = layout.findViewById(R.id.add_account_bank_code);
        EditText accountPin = layout.findViewById(R.id.add_account_pin);

        // Set handler for buttons
        builder.setView(layout);

        builder.setPositiveButton("Save", null);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.show();
        final Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasErrors = false;
                final String accountNameString = accountName.getText().toString();
                final String accountPinString = accountPin.getText().toString();
                final String userIdString = userId.getText().toString();
                final String bankCodeText = bankCode.getText().toString();
                if (userIdString.isEmpty()) {
                    userId.setError("User ID must be defined");
                    hasErrors = true;
                }
                if (bankCodeText.isEmpty()) {
                    bankCode.setError("Bank code must be defined");
                    hasErrors = true;
                }
                if (accountPinString.isEmpty()) {
                    accountPin.setError("Pin cannot be empty");
                    hasErrors = true;
                }
                if (accountNameString.isEmpty()) {
                    accountName.setError("Account name cannot be empty");
                    hasErrors = true;
                }
                if (hasErrors) {
                    return;
                }
                final Integer userIdNumber = Integer.parseInt(userIdString);
                final Integer bankCodeNumber = Integer.parseInt(bankCodeText);
                final StringBuilder obfuscatedStringBuilder = new StringBuilder();
                for (int i = 0; i < userIdString.length(); ++i) {
                    if (i < userIdString.length() - 4) {
                        obfuscatedStringBuilder.append("*");
                    } else {
                    }
                    obfuscatedStringBuilder.append(userIdString.charAt(i));
                }
                // TODO add HBCI API and retrieve actual accounts, save them in the database. Will require progress indicator and more dialogs.

                final Account account = new Account(accountNameString, (float) (Math.random()*800F), obfuscatedStringBuilder.toString(), ColorUtil.getRandomColor(context.getResources()));
                accountRepository.addAccount(account);
                dialog.dismiss();
            }
        });
    }

    public static Spinner createBudgetDialog(View view, Context context, ViewGroup parentGroup, LayoutInflater inflater,
                                             AccountSpinnerAdapter accountSpinnerAdapter,
                                             AccountRepository accountRepository) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View layout = inflater.inflate(R.layout.add_budget_dialog, parentGroup, false);
        // Input data
        EditText budgetName = layout.findViewById(R.id.add_budget_name);
        EditText budgetTotal = layout.findViewById(R.id.add_budget_total);
        Spinner accountSpinner = layout.findViewById(R.id.add_budget_account);
        accountSpinner.setAdapter(accountSpinnerAdapter);
        accountSpinner.setOnItemSelectedListener(setUpAccountSpinnerListener(accountSpinner, inflater, accountRepository, null, null, null));
        Spinner frequencySpinner = layout.findViewById(R.id.add_budget_frequency);
        frequencySpinner.setAdapter(new BudgetFrequencyAdapter(inflater));

        // Set handler for buttons
        builder.setView(layout);

        builder.setPositiveButton("Save", null);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.show();
        final Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasErrors = false;
                final String budgetNameString = budgetName.getText().toString();
                final String amountString = budgetTotal.getText().toString();
                if (budgetNameString.isEmpty()) {
                    budgetName.setError("Budget name must be defined");
                    hasErrors = true;
                }
                if (amountString.isEmpty()) {
                    budgetTotal.setError("Budget total must be defined");
                    hasErrors = true;
                }
                Double total = null;
                try {
                    total = NumberFormat.getInstance(Locale.getDefault()).parse(amountString).doubleValue();
                } catch (ParseException e) {
                    budgetTotal.setError("Invalid input");
                    hasErrors = true;
                }
                Account account = (Account) accountSpinnerAdapter.getItem(accountSpinner.getSelectedItemPosition() - 1);
                long selectedAccount = NO_SELECTION;
                if (account != null) {
                    selectedAccount = account.getId();
                } else {
                    hasErrors = true;
                }

                final BudgetFrequency frequency = (BudgetFrequency) frequencySpinner.getSelectedItem();

                if (selectedAccount == NO_SELECTION) {
                    hasErrors = true;
                    Snackbar.make(parentGroup, "No account selected", Snackbar.LENGTH_SHORT).show();
                }
                if (selectedAccount == ADD_ACCOUNT_ID) {
                    hasErrors = true;
                    Snackbar.make(parentGroup, "No account selected", Snackbar.LENGTH_SHORT).show();
                }
                if (frequency == null) {
                    hasErrors = true;
                    Snackbar.make(parentGroup, "Failed to set frequency", Snackbar.LENGTH_SHORT).show();
                }
                if (hasErrors) {
                    return;
                }
                final Budget budget = new Budget(budgetNameString, 0, total.floatValue(), ColorUtil.getRandomColor(context.getResources()), "€", frequency);
                budget.setLinkedItemId((int) selectedAccount);
                try {
                    accountRepository.addBudget(budget);
                } catch (InterruptedException | TimeoutException | ExecutionException e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Failed to save budget", 5000).show();
                }
                dialog.dismiss();
            }
        });
        return accountSpinner;
    }

    public static AdapterView.OnItemSelectedListener setUpAccountSpinnerListener(Spinner accountSpinner, LayoutInflater inflater, AccountRepository accountRepository,
                                                                                 @Nullable BudgetSpinnerAdapter budgetSpinnerAdapter, @Nullable LifecycleOwner lifecycle, @Nullable Spinner budgetSpinner) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id == ADD_ACCOUNT_ID) {
                    accountSpinner.setSelection(0);
                    // Show add account AlertDialog
                    final Context context = parent.getContext();
                    DialogUtil.buildAccountDialog(context, inflater, accountRepository, parent);
                } else {
                    if (budgetSpinnerAdapter != null && id != NO_SELECTION) {
                        Account account = (Account) accountSpinner.getItemAtPosition(position - 1);
                        accountRepository.getBudgetsForAccount(account.getId()).observe(lifecycle, new Observer<List<Budget>>() {
                            @Override
                            public void onChanged(List<Budget> budgets) {
                                budgetSpinnerAdapter.setBudgets(budgets);
                            }
                        });
                    }
                    // do not directly save to receipt (do it on save button press instead), simply set the drop down item.
                    // TODO: when saving the receipt, use current view state as account id?
                    //receiptModel.setAccountId(id);
                    // Set the account reference in the receipt (TODO add key to receipt schema)
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //NOOP
            }
        };
    }
}
