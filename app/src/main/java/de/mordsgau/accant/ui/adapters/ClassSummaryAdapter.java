package de.mordsgau.accant.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.mordsgau.accant.R;
import de.mordsgau.accant.model.atomic.ClassSummaryRow;
import de.mordsgau.accant.model.metadata.ClassSummary;
import de.mordsgau.accant.ui.view.CircleChartView;

public class ClassSummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /* View types */
    private final int TYPE_HEADER = 0;
    private final int TYPE_ROW = 1;


    private final ClassSummary classSummary;
    private Context context;

    public ClassSummaryAdapter(final ClassSummary classSummary) {
        this.classSummary = classSummary;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_HEADER) {
            final View view = LayoutInflater.from(context).inflate(R.layout.class_summary_header, parent, false);
            return new SummaryHeaderHolder(view);
        } else if (viewType == TYPE_ROW) {
            final View view = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.class_summary_row, parent, false);
            return new SummaryRowHolder(view);
        }
        throw new IllegalArgumentException("Illegal view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SummaryHeaderHolder) {
            SummaryHeaderHolder headerHolder = (SummaryHeaderHolder) holder;
            headerHolder.headerText.setText(classSummary.getTitle());
            headerHolder.circleChart.setData(classSummary.getColors(), classSummary.getFractions());

            //headerHolder.circleChartText.setText("10000 €");

            /*final Sunburst sunburst = AnyChart.sunburst();
            // Add data to chart
            List<DataEntry> chartData = new ArrayList<>();
            for (ClassSummaryRow summaryRow : classSummary.getRows()) {
                final String parentClass = summaryRow.getParentClass();
                if(parentClass != null) {
                    chartData.add(new ClassDataEntry(summaryRow.getRowTitle(), summaryRow.getRowTitle(), parentClass, (int) summaryRow.getRelativeAmount()));
                } else {
                    chartData.add(new ClassDataEntry(summaryRow.getRowTitle(), summaryRow.getRowTitle(), (int) summaryRow.getRelativeAmount()));
                }
            }*/

        } else if (holder instanceof SummaryRowHolder) {
            SummaryRowHolder rowHolder = (SummaryRowHolder) holder;
            final ClassSummaryRow currentRow = classSummary.getRows().get(position - 1);
            // Set background color
            final Resources resources = holder.itemView.getResources();
            final Drawable drawable = resources.getDrawable(R.drawable.rounded_corner);
            // TODO: use proper colors here, determine at beginning, store
            final int color = currentRow.getColor();
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            ((SummaryRowHolder) holder).header.setBackground(drawable);

            rowHolder.header.setText(currentRow.getRowTitle());
            rowHolder.subHeader.setText(currentRow.getRowSubtitle());
            rowHolder.percentage.setText(currentRow.getRelativeAmountString());
            // TODO color bar, unit type per account?
        }
    }

    @Override
    public int getItemCount() {
        // row count + header
        return classSummary.getRows().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ROW;
    }

    public class SummaryHeaderHolder extends RecyclerView.ViewHolder {
        private final TextView headerText;
        private final CircleChartView circleChart;

        SummaryHeaderHolder(@NonNull View itemView) {
            super(itemView);
            this.headerText = itemView.findViewById(R.id.classSummaryHeader);
            this.circleChart = itemView.findViewById(R.id.classSummaryChart);
        }
    }

    public class SummaryRowHolder extends RecyclerView.ViewHolder {
        private final TextView header;
        private final TextView subHeader;
        private final TextView percentSign;
        private final TextView percentage;

        public SummaryRowHolder(@NonNull View itemView) {
            super(itemView);
            this.header = itemView.findViewById(R.id.summaryRowHeader);
            this.subHeader = itemView.findViewById(R.id.summaryRowSubheader);
            this.percentSign = itemView.findViewById(R.id.summaryRowPercentSign);
            this.percentage = itemView.findViewById(R.id.summaryRowPercentage);
        }

    }

    /*public class ClassDataEntry extends DataEntry {
        ClassDataEntry(String name, String id) {
            setValue("name", name);
            setValue("id", id);
        }

        ClassDataEntry(String name, String id, String parent) {
            setValue("name", name);
            setValue("id", id);
            setValue("parent", parent);
        }

        ClassDataEntry(String name, String id, String parent, int value) {
            setValue("name", name);
            setValue("id", id);
            setValue("parent", parent);
            setValue("value", value);
        }

        ClassDataEntry(String name, String id, int value) {
            setValue("name", name);
            setValue("id", id);
            setValue("value", value);
        }
    }*/
}
