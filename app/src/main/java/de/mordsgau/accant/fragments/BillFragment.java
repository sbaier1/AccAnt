package de.mordsgau.accant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import de.mordsgau.accant.R;
import de.mordsgau.accant.db.ReceiptRepository;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.ui.adapters.BillFragmentReceiptAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BillFragment extends Fragment implements HasSupportFragmentInjector {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    @Inject
    ReceiptRepository receiptRepository;

    @Inject
    ExecutorService executorService;

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BillFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BillFragment newInstance(int columnCount) {
        BillFragment fragment = new BillFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_item_list, container, false);

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view;
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final BillFragmentReceiptAdapter receiptAdapter = new BillFragmentReceiptAdapter();
        recyclerView.setAdapter(receiptAdapter);
            receiptRepository.getReceipts().observe(this, new Observer<List<Receipt>>() {
                @Override
                public void onChanged(List<Receipt> receipts) {
                    Collections.sort(receipts, new Comparator<Receipt>() {
                        @Override
                        public int compare(Receipt o1, Receipt o2) {
                            return o1.getDate().before(o2.getDate()) ? 1 : -1;
                        }
                    });
                    // TODO need to implement DiffUtil changes here. Potentially many differences.
                    receiptAdapter.setReceipts(receipts);
                    receiptAdapter.notifyDataSetChanged();
                }
            });
        return view;
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
        void onListFragmentInteraction(Receipt receipt);
    }
}
