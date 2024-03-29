package com.example.schedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class LineView extends View {

    private Paint paint  = new Paint();
    private PointF pointA, pointB;
    private String color;
    public LineView(Context context) {
        super(context);
    }
    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.parseColor(color));
        paint.setStrokeWidth(22);
        canvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paint);

    }
    public void setPointA(PointF point) {
        pointA = point;
    }
    public void setPointB(PointF point) {
        pointB = point;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public void draw() {
        invalidate();
        requestLayout();
    }

}