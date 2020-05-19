package de.mordsgau.accant.ui.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class CircleChartArc extends Drawable {

    private final int color;

    private final float fraction;
    private final Paint mPaint;

    public CircleChartArc(int color, float fraction) {
        this.color = color;
        this.fraction = fraction;
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = getBounds();
        int width = b.width();
        int height = b.height();
        boolean horizontal = width > height;

        float currentFraction = 0F;
        int w = 0;
        // FIXME: ensure proper alignment wit h the parent layout and other layouts using a layout snapshot
        final int dimension = Math.min(b.width(),
                b.height());
        final RectF rectF = new RectF(b.left + dimension / 2, b.top, b.left + dimension + dimension / 2, b.top + dimension);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);


    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
