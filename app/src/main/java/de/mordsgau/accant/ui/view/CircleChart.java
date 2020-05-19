package de.mordsgau.accant.ui.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

class CircleChart extends Drawable {

    private final Paint mPaint;
    private final int paddingLeft;
    private final int paddingRight;
    private final int paddingStart;
    private final int paddingEnd;
    private final int paddingTop;
    private final int paddingBottom;
    private final int darkColor;
    private int[] mColors;
    private float[] mFractions;
    private final int gapSize = 2;

    public CircleChart(int paddingLeft, int paddingRight, int paddingStart, int paddingEnd, int paddingTop, int paddingBottom, int darkColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingStart = paddingStart;
        this.paddingEnd = paddingEnd;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.darkColor = darkColor;
    }

    public void setData(int[] colors, float[] fractions) {
        mColors = colors;
        mFractions = fractions;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = getBounds();
        float currentFraction = 0F;
        final int dimension = Math.min(b.width() - paddingLeft - paddingRight - paddingStart - paddingEnd,
                b.height() - paddingBottom - paddingTop - paddingStart - paddingEnd);
        final RectF rectF = new RectF(b.left + paddingLeft + dimension / 2, b.top + paddingTop, b.left + dimension + paddingLeft - paddingRight + dimension / 2, b.top + dimension);
        int w = 0;
        // FIXME: ensure proper alignment with the parent layout and other layouts using a layout snapshot
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        // Draw underlying circle
        mPaint.setColor(darkColor);
        canvas.drawArc(rectF, 0, 360, false, mPaint);

        if (mFractions != null && mColors != null && mColors.length >= mFractions.length) {
            for (int i = 0; i < mFractions.length; i++) {
                mPaint.setColor(mColors[i]);

                w = Math.round(mFractions[i] * 3.6F);

                canvas.drawArc(rectF, currentFraction + gapSize - 90, w - gapSize, false, mPaint);
                currentFraction += w;
            }

            // Draw remaining percentage as dark line
            /*if (mFractions.length > 1 && currentFraction < 360) {
                mPaint.setColor(darkColor);
                canvas.drawArc(rectF, currentFraction + gapSize, 360 - currentFraction - gapSize, false, mPaint);
            }*/
        }


        mPaint.setColor(0xff26282F);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
