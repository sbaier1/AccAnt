package de.mordsgau.accant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

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
import de.mordsgau.accant.model.Section;
import de.mordsgau.accant.model.atomic.RowData;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.ui.adapters.BalanceViewAdapter;
import de.mordsgau.accant.ui.view.CircleChartView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BalanceFragment extends Fragment implements HasSupportFragmentInjector {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    @Inject
    AccountRepository accountRepository;

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;
    private CircleChartView circleChart;
    private TextView circleChartText;

    private int[] mColors;
    private float[] mFractions;
    private float[] emptyFraction;
    private int[] emptyColors;
    private BalanceViewAdapter balanceViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BalanceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BalanceFragment newInstance(int columnCount) {
        BalanceFragment fragment = new BalanceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);

        emptyFraction = new float[0];
        emptyColors = new int[0];

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void animateEntry() {
        if (mColors != null && mFractions != null) {
            circleChart.setData(mColors, mFractions);
        }
    }


    public void tabUnselected() {
        if (circleChart != null) {
            circleChart.setData(emptyColors, emptyFraction);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accounts_header, container, false);
        circleChart = view.findViewById(R.id.classSummaryChart);
        circleChartText = view.findViewById(R.id.circleChartText);
        ImageButton circleChartImage = view.findViewById(R.id.circleChartButton);
        circleChartImage.setVisibility(View.INVISIBLE);

        RecyclerView recyclerView = view.findViewById(R.id.balance_view_recycler);
        balanceViewAdapter = new BalanceViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(balanceViewAdapter);
        // Initialize data holder
        getAccountObserver();

        // Set the adapter
        /*if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new HomeItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }*/
        return view;
    }

    private void getAccountObserver() {
        accountRepository.getAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                Double sum = 0D;
                for (Account account : accounts) {
                    sum += account.getMAmount();
                }
                float fractions[] = new float[accounts.size()];
                int[] colors = new int[accounts.size()];
                for (int i = 0; i < accounts.size(); ++i) {
                    final Account account = accounts.get(i);
                    // We use a percentage in the circle chart
                    fractions[i] = (float) (account.getMAmount() / sum) * 100;
                    colors[i] = account.getMColor();
                }
                if (circleChart != null && circleChartText != null) {
                    circleChart.setData(colors, fractions);
                    mColors = colors;
                    mFractions = fractions;
                    final String format = String.format(Locale.getDefault(), "%.02f €", sum);
                    if (!format.isEmpty()) {
                        circleChartText.setText(format);
                    }
                }
                if(balanceViewAdapter != null) {
                    List<RowData> accountsAlt = new ArrayList<>(accounts);
                    balanceViewAdapter.setmSection(new Section(accountsAlt, "", false));
                    balanceViewAdapter.notifyDataSetChanged();
                }
            }
        });
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
        void onListFragmentInteraction(Account item);
    }
}
