package de.mordsgau.accant.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.mordsgau.accant.R;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.ui.view.FractionBarView;

import static de.mordsgau.accant.ui.CreateReceiptActivity.DEFAULT_LOCALE;

public class BillFragmentReceiptAdapter extends RecyclerView.Adapter {
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
    private List<Receipt> receipts;

    public BillFragmentReceiptAdapter() {
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.layout_account_row, parent, false);
        return new RowViewHolder(constraintLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rvh = (RowViewHolder) holder;
        // TODO: receipts might need a name attribute (maybe the shop?)
        final Receipt receipt = receipts.get(position);
        rvh.amountQualifierText.setVisibility(View.GONE);
        rvh.primaryText.setText("Receipt");
        rvh.secondaryTextObfuscation.setText(DATE_FORMAT.format(receipt.getDate()));
        rvh.secondaryText.setText("");
        rvh.amountText.setText(String.format(DEFAULT_LOCALE, "%.02f", receipt.getSum()));
        rvh.barView.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return receipts != null ? receipts.size() : 0;
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {

        public TextView primaryText;
        public TextView secondaryText;
        public TextView secondaryTextObfuscation;
        public TextView amountText;
        public TextView amountQualifierText;
        public FractionBarView barView;
        public ImageView arrow;

        public RowViewHolder(View itemView) {
            super(itemView);
            primaryText = itemView.findViewById(R.id.account_primary_text);
            secondaryText = itemView.findViewById(R.id.account_secondary_text);
            secondaryTextObfuscation = itemView.findViewById(R.id
                    .account_secondary_text_obfuscation);
            barView = itemView.findViewById(R.id.row_bar_chart);
            amountText = itemView.findViewById(R.id.amount_text);
            amountQualifierText = itemView.findViewById(R.id.amount_qualifier_text);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }
}
