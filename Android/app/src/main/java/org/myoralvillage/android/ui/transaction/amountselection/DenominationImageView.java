package org.myoralvillage.android.ui.transaction.amountselection;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

import java.util.Locale;

public class DenominationImageView extends AppCompatImageView {


    public DenominationImageView(Context context) {
        super(context);
    }

    public DenominationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setCurrencyDenomination(MOVCurrencyDenomination denomination) {
        if(denomination != null) {
            String drawableName = String.format(Locale.getDefault(), "%s_%d", denomination.getCurrency().getCode().toLowerCase(), denomination.getValue());
            setImageResource(getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName()));
        } else {
            setImageResource(R.drawable.bill_add);
        }
    }

    /**
     * SOURCE OF THE CODE BELOW: https://stackoverflow.com/questions/43414865/image-out-of-bounds-after-transformation-on-view
     */
    private double mRotatedWidth;
    private double mRotatedHeight;

    private boolean update() {
        Drawable d = getDrawable();

        if (d == null) {
            return false;
        }

        int drawableWidth = d.getIntrinsicWidth();
        int drawableHeight = d.getIntrinsicHeight();

        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return false;
        }

        double rotationRad = getRotation() / 180 * Math.PI;

        // calculate intrinsic rotated size
        // see diagram

        mRotatedWidth = (Math.abs(Math.sin(rotationRad)) * drawableHeight
                + Math.abs(Math.cos(rotationRad)) * drawableWidth);
        mRotatedHeight = (Math.abs(Math.cos(rotationRad)) * drawableHeight
                + Math.abs(Math.sin(rotationRad)) * drawableWidth);

        return true;
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (update()) {
            double ratio = mRotatedWidth / mRotatedHeight;

            int wMax = Math.min(getDefaultSize(Integer.MAX_VALUE, widthMeasureSpec), getMaxWidth());
            int hMax = Math.min(getDefaultSize(Integer.MAX_VALUE, heightMeasureSpec), getMaxHeight());

            int w = (int) Math.min(wMax, hMax * ratio);
            int h = (int) Math.min(hMax, wMax / ratio);

            setMeasuredDimension(w, h);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    private final float[] values = new float[9];

    protected void onDraw(Canvas canvas) {

        if (update()) {
            int availableWidth = getMeasuredWidth();
            int availableHeight = getMeasuredHeight();

            float scale = (float) Math.min(availableWidth / mRotatedWidth, availableHeight / mRotatedHeight);

            getImageMatrix().getValues(values);

            setScaleX(scale / values[Matrix.MSCALE_X]);
            setScaleY(scale / values[Matrix.MSCALE_Y]);
        }

        super.onDraw(canvas);
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        requestLayout();
    }

}
