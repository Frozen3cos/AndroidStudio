package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

public class CustomButton extends Button {

    private String buttonText;
    private int buttonColor;

    public CustomButton(Context context) {
        super(context);
        init(null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomButton);
            buttonText = ta.getString(R.styleable.CustomButton_buttonText);
            buttonColor = ta.getColor(R.styleable.CustomButton_buttonColor,
                    Color.parseColor("#4CAF50"));
            ta.recycle();
        }
        if (buttonText == null) buttonText = "自定义按钮";
        applyStyle();
    }

    private void applyStyle() {
        setText(buttonText);
        setTextColor(Color.WHITE);
        setTextSize(16);
        setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(buttonColor);
        bg.setCornerRadius(24f);
        setBackground(bg);
    }

    public void setButtonColor(int color) {
        this.buttonColor = color;
        applyStyle();
    }

    public void setButtonText(String text) {
        this.buttonText = text;
        setText(text);
    }
}