package de.mordsgau.accant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import de.mordsgau.accant.R;
import de.mordsgau.accant.db.AccountRepository;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.metadata.Budget;
import de.mordsgau.accant.ui.DialogUtil;
import de.mordsgau.accant.ui.NonScrollableFlexboxLayoutManager;
import de.mordsgau.accant.ui.adapters.AccountSpinnerAdapter;
import de.mordsgau.accant.ui.adapters.BudgetFragmentRowAdapter;
import de.mordsgau.accant.ui.view.FractionBarView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Budget view, shows budgets divided into their classes and summaries.
 */
public class BudgetFragment extends Fragment implements HasSupportFragmentInjector {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private NumberFormat currencyInstance;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;


    // Budgets are stored in the account repository as well, as they are closely related
    @Inject
    AccountRepository accountRepository;

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    private volatile List<Budget> mBudgetsWeekly;
    private volatile List<Budget> mBudgetsYearly;
    private volatile List<Budget> mBudgetsMonthly;

    private AccountSpinnerAdapter mAccountSpinnerAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BudgetFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BudgetFragment newInstance(int columnCount) {
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
        currencyInstance = NumberFormat.getCurrencyInstance(Locale.getDefault());

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final FloatingActionButton floatingActionButton = getView().findViewById(R.id.floatingActionButton);
        if (floatingActionButton != null) {
            floatingActionButton.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // FIXME this is taking too long and blocking the UI thread. maybe set empty adapter immediately and run the observers and setup in executorservice?
        super.onCreateView(inflater, container, savedInstanceState);
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.budget_fragment_layout, container, false);
        mAccountSpinnerAdapter = new AccountSpinnerAdapter(getLayoutInflater());
        accountRepository.getAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                mAccountSpinnerAdapter.setAccounts(accounts);
            }
        });
        accountRepository.getBudgetsWeekly().observe(this, new Observer<List<Budget>>() {
            @Override
            public void onChanged(List<Budget> budgets) {
                mBudgetsWeekly = budgets;
                Log.d("ASD", "new sizew: " + budgets);
                updateSection(R.string.weekly_budget_section_name, view.findViewById(R.id.section_weekly), mBudgetsWeekly, view);
            }
        });
        accountRepository.getBudgetsMonthly().observe(this, new Observer<List<Budget>>() {
            @Override
            public void onChanged(List<Budget> budgets) {
                mBudgetsMonthly = budgets;
                Log.d("ASD", "new sizem: " + budgets);
                updateSection(R.string.monthly_budget_section_name, view.findViewById(R.id.section_monthly), mBudgetsMonthly, view);
            }
        });
        accountRepository.getBudgetsYearly().observe(this, new Observer<List<Budget>>() {
            @Override
            public void onChanged(List<Budget> budgets) {
                mBudgetsYearly = budgets;
                Log.d("ASD", "new size: " + budgets);
                updateSection(R.string.yearly_budget_section_name, view.findViewById(R.id.section_yearly), mBudgetsYearly, view);
            }
        });
        return view;
    }

    private void updateSection(int sectionNameStringId, ConstraintLayout layout, List<Budget> budgets, ConstraintLayout parentView) {
        // TODO all of this needs to be replaced with a recyclerview.
        RecyclerView recyclerView = layout.findViewById(R.id.budget_fragment_recycler);
        recyclerView.setLayoutManager(new NonScrollableFlexboxLayoutManager(getContext()));
        final BudgetFragmentRowAdapter adapter = new BudgetFragmentRowAdapter();
        recyclerView.setAdapter(adapter);
        ConstraintLayout noBudget = parentView.findViewById(R.id.noBudgetView);
        ConstraintLayout container = parentView.findViewById(R.id.budgetContent);
        if (noBudget.getVisibility() == VISIBLE) {
            if (((mBudgetsMonthly != null && mBudgetsMonthly.size() > 0) ||
                    (mBudgetsWeekly != null && mBudgetsWeekly.size() > 0) ||
                    (mBudgetsYearly != null && mBudgetsYearly.size() > 0))) {
                // If any category has budgets, we hide the "no budget" view.
                noBudget.setVisibility(GONE);
                noBudget.setOnClickListener(null);
                container.setVisibility(View.VISIBLE);
            } else {
                noBudget.setVisibility(View.VISIBLE);
                container.setVisibility(GONE);
                noBudget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.createBudgetDialog(noBudget, getContext(), parentView, getLayoutInflater(), mAccountSpinnerAdapter, accountRepository);
                    }
                });
                return;
            }
        }
        if (budgets.size() == 0) {
            layout.setVisibility(GONE);
            return;
        } else {
            if (layout.getVisibility() == GONE) {
                layout.setVisibility(View.VISIBLE);
            }
        }
        TextView sectionName = layout.findViewById(R.id.section_name);
        sectionName.setText(sectionNameStringId);
        double totalCurrent = 0D;
        double totalMax = 0D;
        int colors[] = new int[budgets.size()];
        float fractions[] = new float[budgets.size()];

        for (Budget budget : budgets) {
            totalCurrent += budget.getmCurrAmount();
            totalMax += budget.getMLimitAmount();
        }

        for (int i = 0; i < budgets.size(); ++i) {
            final Budget budget = budgets.get(i);
            colors[i] = budget.getMColor();
            fractions[i] = (float) (budget.getmCurrAmount() / totalCurrent);
        }
        TextView sectionTotalCurrent = layout.findViewById(R.id.section_total_1);
        TextView sectionTotalMax = layout.findViewById(R.id.section_total_2);
        sectionTotalCurrent.setText(currencyInstance.format(totalCurrent));
        sectionTotalMax.setText(currencyInstance.format(totalMax));

        FractionBarView barChart = layout.findViewById(R.id.section_bar_chart);
        barChart.setData(colors, fractions);

        adapter.setBudgets(budgets);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Budget item);
    }
}
