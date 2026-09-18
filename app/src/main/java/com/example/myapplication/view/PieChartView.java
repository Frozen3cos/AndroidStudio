package com.example.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义饼图控件（环形图）
 * 用法：
 *   PieChartView chart = findViewById(R.id.pieChart);
 *   List<PieChartView.Slice> data = new ArrayList<>();
 *   data.add(new PieChartView.Slice("发出", 10, Color.GREEN));
 *   chart.setData(data);
 */
public class PieChartView extends View {

    /** 单个扇区数据 */
    public static class Slice {
        public String label;
        public float value;
        public int color;

        public Slice(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    private final List<Slice> slices = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        centerTextPaint.setColor(Color.parseColor("#666666"));
        centerTextPaint.setTextSize(32f);
        centerTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(List<Slice> data) {
        slices.clear();
        slices.addAll(data);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float size = Math.min(getWidth(), getHeight()) - 40;
        float radius = size / 2f;

        // 无数据时画灰色圆
        if (slices.isEmpty()) {
            paint.setColor(Color.parseColor("#E0E0E0"));
            canvas.drawCircle(cx, cy, radius, paint);
            return;
        }

        // 计算总数
        float total = 0;
        for (Slice s : slices) total += s.value;
        if (total <= 0) return;

        // 饼图外接矩形
        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius);

        // 逐块绘制
        float startAngle = -90f;
        for (Slice s : slices) {
            float sweep = s.value / total * 360f;
            paint.setColor(s.color);
            canvas.drawArc(rectF, startAngle, sweep, true, paint);

            // 扇区中心写数值
            float midAngle = startAngle + sweep / 2f;
            float textR = radius * 0.65f;
            float tx = (float) (cx + textR * Math.cos(Math.toRadians(midAngle)));
            float ty = (float) (cy + textR * Math.sin(Math.toRadians(midAngle)));
            float offset = (textPaint.descent() + textPaint.ascent()) / 2f;
            canvas.drawText(String.valueOf((int) s.value), tx, ty - offset, textPaint);

            startAngle += sweep;
        }

        // 中心挖一个白圆，形成环形图
        paint.setColor(Color.WHITE);
        float innerR = radius * 0.45f;
        canvas.drawCircle(cx, cy, innerR, paint);

        // 中心写总数
        float tOffset = (centerTextPaint.descent() + centerTextPaint.ascent()) / 2f;
        canvas.drawText(String.valueOf((int) total), cx, cy - tOffset, centerTextPaint);
    }
}