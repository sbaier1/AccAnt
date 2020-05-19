package de.mordsgau.accant.ui;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.AndroidInjection;
import de.mordsgau.accant.R;
import de.mordsgau.accant.db.AccountRepository;
import de.mordsgau.accant.db.ReceiptRepository;
import de.mordsgau.accant.di.ViewModelFactory;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.model.atomic.ReceiptItem;
import de.mordsgau.accant.model.atomic.ReceiptItemTag;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.metadata.Budget;
import de.mordsgau.accant.ui.adapters.AccountSpinnerAdapter;
import de.mordsgau.accant.ui.adapters.BudgetSpinnerAdapter;
import de.mordsgau.accant.ui.adapters.ReceiptItemAdapter;
import de.mordsgau.accant.ui.viewmodel.ReceiptModel;

import static de.mordsgau.accant.ui.DialogUtil.createBudgetDialog;
import static de.mordsgau.accant.ui.DialogUtil.setUpAccountSpinnerListener;
import static de.mordsgau.accant.ui.adapters.BudgetSpinnerAdapter.ADD_BUDGET_ID;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;

public class CreateReceiptActivity extends AppCompatActivity {

    static final String RECEIPT_ID_KEY = "receipt-id";
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private ReceiptModel receiptModel;

    private Context mContext;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    ReceiptRepository receiptRepository;

    @Inject
    AccountRepository accountRepository;
    private Spinner mBudgetSpinner;
    private Spinner mAccountSpinner;
    private AccountSpinnerAdapter mAccountSpinnerAdapter;
    private BudgetSpinnerAdapter mBudgetSpinnerAdapter;
    private boolean discardOnBackButton = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        this.mContext = this;

        receiptModel = ViewModelProviders.of(this, viewModelFactory).get(ReceiptModel.class);
        final Intent intent = getIntent();
        if (intent != null) {
            final Bundle extras = intent.getExtras();
            if (extras != null) {
                final Integer receiptId = (Integer) extras.get(RECEIPT_ID_KEY);
                if (receiptId != null) {
                    discardOnBackButton = true;
                    receiptModel.init(receiptId);
                } else {
                    saveAndInitEmptyReceipt();
                }
            } else {
                saveAndInitEmptyReceipt();
            }
        } else {
            saveAndInitEmptyReceipt();
        }
        setContentView(R.layout.activity_create_receipt);
    }

    private void saveAndInitEmptyReceipt() {
        Receipt receiptCaptured = new Receipt(0D, EMPTY_LIST, new Date(), EMPTY_MAP);
        final AtomicInteger receiptId = new AtomicInteger();
        try {
            receiptId.set(receiptRepository.saveReceipt(receiptCaptured).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        receiptModel.init(receiptId.get());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Tag suggestion view init */
        final ChipGroup tagAddView = findViewById(R.id.suggestedTags);
        final LiveData<List<ReceiptItemTag>> tags = receiptRepository.getTags();
        tags.observe(this, new Observer<List<ReceiptItemTag>>() {
            @Override
            public void onChanged(List<ReceiptItemTag> receiptItemTags) {
                updateRecommendedTags(tagAddView, receiptItemTags);
            }
        });
        final View.OnClickListener addTagClickListener = getAddTagClickListener();
        tagAddView.setOnClickListener(addTagClickListener);

        /* Account, budget spinner init */
        mAccountSpinner = findViewById(R.id.account_spinner);
        final LiveData<List<Account>> accountData = accountRepository.getAccounts();

        mAccountSpinnerAdapter = new AccountSpinnerAdapter(getLayoutInflater());
        mAccountSpinner.setAdapter(mAccountSpinnerAdapter);
        accountData.observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                mAccountSpinnerAdapter.setAccounts(accounts);
                if (accounts != null && accounts.size() > 0) {
                    mAccountSpinner.setSelection(1);
                }
            }
        });
        mBudgetSpinner = findViewById(R.id.budget_spinner);
        mBudgetSpinnerAdapter = new BudgetSpinnerAdapter(getLayoutInflater());
        final AdapterView.OnItemSelectedListener accountSpinnerItemListener = setUpAccountSpinnerListener(mAccountSpinner, getLayoutInflater(), accountRepository, mBudgetSpinnerAdapter, this, mBudgetSpinner);
        mAccountSpinner.setOnItemSelectedListener(accountSpinnerItemListener);
        mBudgetSpinner.setAdapter(mBudgetSpinnerAdapter);
        mBudgetSpinner.setOnItemSelectedListener(setUpBudgetSpinner(mAccountSpinnerAdapter, mBudgetSpinner));

        /* Recycler list init, total button text updates */
        final TextView total = findViewById(R.id.totalSum);
        final RecyclerView recyclerView = findViewById(R.id.receipt_items);
        final ReceiptItemAdapter adapter = new ReceiptItemAdapter(receiptModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        receiptModel.getReceipt().observe(this, new Observer<Receipt>() {
            @Override
            public void onChanged(Receipt receipt) {
                if (receipt == null) {
                    return;
                }
                // TODO: make a diff and only update values that have changed (DiffUtil)
                adapter.notifyDataSetChanged();
                total.setText(String.format(DEFAULT_LOCALE, "%.02f", receipt.getSum()));
            }
        });
        // Initially draw data TODO necessary?
        adapter.notifyDataSetChanged();

        /* Save, add item button callbacks */
        FloatingActionButton saveButton = findViewById(R.id.saveButton);
        addSaveListener(saveButton);

        final ItemTouchHelper.SimpleCallback touchCallback = getSwipeCallback(recyclerView);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton addButton = findViewById(R.id.addButton);
        addAddButtonListener(adapter, addButton);
    }

    private AdapterView.OnItemSelectedListener setUpBudgetSpinner(AccountSpinnerAdapter accountSpinnerAdapter, Spinner budgetSpinner) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id == ADD_BUDGET_ID) {
                    budgetSpinner.setSelection(0);
                    // Show add account AlertDialog
                    final Context context = parent.getContext();
                    final LayoutInflater inflater = getLayoutInflater();
                    createBudgetDialog(view, context, parent, inflater, accountSpinnerAdapter, accountRepository);
                } else {
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

    private View.OnClickListener getAddTagClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Add tag");
                final EditText edit = new EditText(mContext);
                edit.setSingleLine();
                edit.setInputType(InputType.TYPE_CLASS_TEXT);
                FrameLayout container = new FrameLayout(mContext);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                edit.setLayoutParams(params);
                container.addView(edit);
                builder.setView(container);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String tagName = edit.getText().toString();
                        final ReceiptItemTag newTag = new ReceiptItemTag(ColorUtil.getRandomColor(getResources()), tagName, 1D);
                        receiptRepository.addTag(newTag);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        };
    }

    private void addAddButtonListener(ReceiptItemAdapter adapter, FloatingActionButton addButton) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ReceiptItem newItem = new ReceiptItem("", 0D, new ArrayList<>());
                receiptModel.addItem(newItem);
                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(receiptModel.getReceipt().getValue().getItems().size() - 1);
            }
        });
    }

    private ItemTouchHelper.SimpleCallback getSwipeCallback(RecyclerView recyclerView) {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView1, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //recyclerView.removeViewAt(viewHolder.getAdapterPosition());
                final Receipt value = receiptModel.getReceipt().getValue();

                final ReceiptItem receiptItem = value.getItems().get(viewHolder.getAdapterPosition());
                // FIXME delete queries
                receiptModel.removeItem(receiptItem.getReceiptItem());
                //value.getItems().remove(viewHolder.getAdapterPosition());
                recyclerView.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());
                //recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    private void addSaveListener(FloatingActionButton saveButton) {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Receipt currentReceipt = receiptModel.getReceipt().getValue();
                if (currentReceipt != null && !currentReceipt.getItems().isEmpty()) {
                    final int accountPosition = mAccountSpinner.getSelectedItemPosition() - 1;
                    if (accountPosition >= 0) {
                        Account selectedAccount = (Account) mAccountSpinnerAdapter.getItem(accountPosition);
                        if (selectedAccount != null) {
                            currentReceipt.getReceipt().setAccountId(selectedAccount.getId());
                            // TODO add a transaction to the account, +-0 with receipt linked
                        }
                    }
                    if (currentReceipt.getSum() == 0D) {
                        // TODO: warn user about 0 sum receipt or throw error.
                    }
                    final int positionBudget = mBudgetSpinner.getSelectedItemPosition() - 1;
                    if (positionBudget >= 0) {
                        Budget selectedBudget = (Budget) mBudgetSpinnerAdapter.getItem(positionBudget);
                        if (selectedBudget != null) {
                            currentReceipt.getReceipt().setBudgetId(selectedBudget.getId());
                            final double newCurrentAmount = selectedBudget.getmCurrAmount() + currentReceipt.getSum();
                            if (newCurrentAmount > selectedBudget.getMLimitAmount()) {
                                // Warn user about transaction
                                AlertDialog.Builder dialogBuilder = createRaiseLimitDialogue(v, currentReceipt, selectedBudget, (float) newCurrentAmount);
                                dialogBuilder.show();
                            } else {
                                selectedBudget.setMCurrAmount((float) newCurrentAmount);
                                accountRepository.updateBudget(selectedBudget);
                                saveReceipt(currentReceipt);
                            }
                        } else {
                            saveReceipt(currentReceipt);
                        }
                    } else {
                        saveReceipt(currentReceipt);
                    }
                }
            }
        });
    }

    private AlertDialog.Builder createRaiseLimitDialogue(View v, Receipt currentReceipt, Budget selectedBudget, float newCurrentAmount) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        dialogBuilder.setMessage("Receipt exceeds budget limit");
        dialogBuilder.setPositiveButton("Raise limit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Raise limit, save
                selectedBudget.setMCurrAmount(newCurrentAmount);
                selectedBudget.setMLimitAmount(newCurrentAmount);
                accountRepository.updateBudget(selectedBudget);
                saveReceipt(currentReceipt);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // NOOP
            }
        });
        return dialogBuilder;
    }

    private void saveReceipt(Receipt currentReceipt) {
        final Future<?> task = receiptRepository.updateReceipt(currentReceipt.getReceipt());
        try {
            task.get(10, TimeUnit.SECONDS);
            //TODO error handling
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        finish();
    }

    private void updateRecommendedTags(ChipGroup suggestedTags, List<ReceiptItemTag> tags) {
        suggestedTags.removeAllViews();
        for (ReceiptItemTag tag : tags) {
            final Chip tagChip = new Chip(this);
            tagChip.setText(tag.getTagName());
            tagChip.setTextAppearance(R.style.Subtitle3);
            if (tag.getColor() != 0) {
                tagChip.getBackground().setColorFilter(tag.getColor(), PorterDuff.Mode.MULTIPLY);
                receiptModel.setColorForTag(tag.getTagName(), tag.getColor());
            }
            tagChip.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Chip chip = (Chip) v;
                    final View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
                    final ClipData.Item item = new ClipData.Item(chip.getText());
                    final ClipData data = new ClipData(chip.getText(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                    return v.startDrag(data,
                            dragShadowBuilder,
                            null,
                            0);
                }
            });
            suggestedTags.addView(tagChip);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!discardOnBackButton) {
            final Receipt receipt = receiptModel.getReceipt().getValue();
            if (receipt != null && receipt.getSum() == 0D) {
                receiptRepository.deleteReceipt(receipt);
            }
            super.onBackPressed();
            return;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(getString(R.string.discard_receipt_prompt));
        dialogBuilder.setPositiveButton(R.string.discard_receipt_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Receipt receipt = receiptModel.getReceipt().getValue();
                if (receipt != null) {
                    receiptRepository.deleteReceipt(receipt);
                }
                CreateReceiptActivity.super.onBackPressed();
            }
        });
        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        dialogBuilder.show();
    }
}
