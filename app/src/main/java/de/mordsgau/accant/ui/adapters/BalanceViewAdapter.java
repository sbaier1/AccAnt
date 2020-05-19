/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mordsgau.accant.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import de.mordsgau.accant.R;
import de.mordsgau.accant.model.Section;
import de.mordsgau.accant.model.atomic.RowData;
import de.mordsgau.accant.ui.view.FractionBarView;

public class BalanceViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Section mSection;

    public BalanceViewAdapter() {
    }

    public void setmSection(Section mSection) {
        this.mSection = mSection;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        RecyclerView.ViewHolder vh = null;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_account_row,
                parent, false);
        vh = new RowViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rvh = (RowViewHolder) holder;
        RowData rowData = mSection.getItems().get(position);
        rvh.primaryText.setText(rowData.getRowName());
        rvh.secondaryText.setText(rowData.getRowSecondaryString());
        rvh.secondaryTextObfuscation.setVisibility(rowData.showSecondaryStringObfuscation() ?
                View.VISIBLE : View.GONE);

        rvh.amountText.setText(rowData.getRowAmountString());

        int[] colors = new int[1];
        float[] fractions = new float[1];
        colors[0] = rowData.getRowColor();
        fractions[0] = rowData.getRowAmount() / rowData.getRowLimitAmount();

        rvh.barView.setData(colors, fractions);

        if (rowData.getAmountQualifierString() == null) {
            rvh.amountQualifierText.setVisibility(View.GONE);
            rvh.arrow.setVisibility(View.VISIBLE);
        } else {
            rvh.amountQualifierText.setVisibility(View.VISIBLE);
            rvh.amountQualifierText.setText(rowData.getAmountQualifierString());
            rvh.arrow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mSection != null ? mSection.getItems().size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
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
