package de.mordsgau.accant.model.metadata;

import java.util.List;

import androidx.annotation.NonNull;
import de.mordsgau.accant.model.atomic.ClassSummaryRow;

/**
 * Represents the summary of classes and their percentage of money used for each of them.
 */
public class ClassSummary {
    @NonNull
    private final String title;

    private final List<ClassSummaryRow> rows;

    public ClassSummary(final List<ClassSummaryRow> rows, final String title) {
        this.rows = rows;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<ClassSummaryRow> getRows() {
        return rows;
    }

    public int[] getColors() {
        int[] colors = new int[rows.size()];
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = rows.get(i).getColor();
        }
        return colors;
    }

    public float[] getFractions() {
        float[] colors = new float[rows.size()];
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = rows.get(i).getRelativeAmount();
        }
        return colors;
    }
}
