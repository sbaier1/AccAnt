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
package de.mordsgau.accant.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import de.mordsgau.accant.R;

public class CircleChartView extends View {

    private final int darkColor;
    //private List<CircleChart> circleCharts;
    CircleChart circleChart;

    public CircleChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        darkColor = ResourcesCompat.getColor(context.getResources(), R.color.backgroundDark, null);
        this.circleChart = new CircleChart(getPaddingLeft(), getPaddingRight(), getPaddingStart(), getPaddingEnd(),
                getPaddingTop(), getPaddingBottom(), darkColor);
        // FIXME: create a layout in here that aligns some text views and an imageview with the circle.

        setBackground(circleChart);
    }

    public void setData(@NonNull int[] colors, @NonNull float[] fractions) {
        //circleChart.setData(colors, fractions);
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        final DecelerateInterpolator interpolator = new DecelerateInterpolator(5F);
        //final BounceInterpolator interpolator = new BounceInterpolator();
        valueAnimator.setStartDelay(300);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] newValues = new float[fractions.length];
                for(int i = 0; i < fractions.length; ++i) {
                    newValues[i] = fractions[i] * animation.getAnimatedFraction();
                }
                circleChart.setData(colors, newValues);
                invalidate();
            }
        });
        // TODO move this to another thread
        valueAnimator.start();
    }
}
