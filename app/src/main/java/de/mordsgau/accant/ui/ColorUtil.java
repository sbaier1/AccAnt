package de.mordsgau.accant.ui;

import android.content.res.Resources;

import androidx.core.content.res.ResourcesCompat;
import de.mordsgau.accant.R;

public class ColorUtil {

    private static int colorIndex = -1;

    public static int getRandomColor(Resources resources) {
        ++colorIndex;
        if(colorIndex > 11) {
            // Just rotate colors back for now, in the future: multiply / filter the colors to get variety.
            colorIndex = 0;
        }
        switch (colorIndex) {
            case 0:
                return ResourcesCompat.getColor(resources, R.color.tagColor1, null);
            case 1:
                return ResourcesCompat.getColor(resources, R.color.tagColor2, null);
            case 2:
                return ResourcesCompat.getColor(resources, R.color.tagColor3, null);
            case 3:
                return ResourcesCompat.getColor(resources, R.color.tagColor4, null);
            case 4:
                return ResourcesCompat.getColor(resources, R.color.tagColor5, null);
            case 5:
                return ResourcesCompat.getColor(resources, R.color.tagColor6, null);
            case 6:
                return ResourcesCompat.getColor(resources, R.color.tagColor7, null);
            case 7:
                return ResourcesCompat.getColor(resources, R.color.tagColor8, null);
            case 8:
                return ResourcesCompat.getColor(resources, R.color.tagColor9, null);
            case 9:
                return ResourcesCompat.getColor(resources, R.color.tagColor10, null);
            case 10:
                return ResourcesCompat.getColor(resources, R.color.tagColor11, null);
            case 11:
                return ResourcesCompat.getColor(resources, R.color.tagColor12, null);
        }
        return 0;
    }
}
