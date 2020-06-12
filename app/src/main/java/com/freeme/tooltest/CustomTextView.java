package com.freeme.tooltest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    private Paint p = new Paint();
    private final float DEI = 20f;
    private int[] colors = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i = 0;
        while (getWidth()- DEI * i > DEI) {
            p.setColor(colors[i%4]);
            canvas.drawRect(getLeft()+DEI*i, getTop(), getLeft()+DEI*(i+1), getBottom(), p);
            i++;
        }

        super.onDraw(canvas);
    }
}
