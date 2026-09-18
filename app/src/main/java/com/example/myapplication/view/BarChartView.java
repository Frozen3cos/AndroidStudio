package com.example.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义柱状图控件
 */
public class BarChartView extends View {

    public static class Bar {
        public String label;
        public float value;
        public int color;

        public Bar(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    private final List<Bar> bars = new ArrayList<>();
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setColor(Color.parseColor("#666666"));
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        axisPaint.setColor(Color.parseColor("#DDDDDD"));
        axisPaint.setStrokeWidth(2f);
    }

    public void setData(List<Bar> data) {
        bars.clear();
        bars.addAll(data);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bars.isEmpty()) return;

        int w = getWidth();
        int h = getHeight();
        float pl = 40f, pr = 40f, pt = 40f, pb = 60f;

        float chartW = w - pl - pr;
        float chartH = h - pt - pb;

        // 最大值
        float maxValue = 0;
        for (Bar b : bars) maxValue = Math.max(maxValue, b.value);
        if (maxValue <= 0) maxValue = 1;

        // X 轴
        canvas.drawLine(pl, h - pb, w - pr, h - pb, axisPaint);

        // 每根柱子的宽度和间距
        float gap = chartW / bars.size();
        float barWidth = gap * 0.5f;

        for (int i = 0; i < bars.size(); i++) {
            Bar b = bars.get(i);
            float barH = b.value / maxValue * chartH;
            float left = pl + gap * i + (gap - barWidth) / 2f;
            float top = h - pb - barH;
            float right = left + barWidth;
            float bottom = h - pb;

            // 画柱子
            barPaint.setColor(b.color);
            canvas.drawRect(left, top, right, bottom, barPaint);

            // 数值
            canvas.drawText(String.valueOf((int) b.value),
                    (left + right) / 2f, top - 8f, textPaint);

            // 标签
            canvas.drawText(b.label,
                    (left + right) / 2f, h - pb + 36f, textPaint);
        }
    }
}