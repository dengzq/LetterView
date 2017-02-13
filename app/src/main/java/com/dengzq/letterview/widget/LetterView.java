package com.dengzq.letterview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dengzq.letterview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Company: tsingning
 * Created by dengzq
 * Created time: 2017/2/4
 * Package_name: com.dengzq.dengzqtestapp.widget.LetterView
 * Description : 字母控件
 */

public class LetterView extends View {
    private static final String TAG = "LetterView";
    private List<String> letters;                 //字母list
    private String       letter;                  //字母
    private Paint        circlePaint;             //边框画笔
    private Paint        letterPaint;             //字母画笔
    private boolean      checked;                 //是否被选择了
    private boolean      finished;                //是否结束选择
    private boolean      seted;                   //是否被设置了字母
    private int strokeColor        = 0xFFC4C4C4;  //边框颜色
    private int checkColor         = 0xFFEEAD0E;  //默认的点击之后的颜色
    private int letterDefaultColor = 0xFF404040;  //文字颜色
    private int letterCheckedColor = 0xFF404040;  //点击之后的文字颜色
    private int letterFinishColor  = 0xFF404040;  //完成文字颜色
    private int     letterSize;
    private int     strokeWidth;
    private Context mContext;

    public LetterView(Context context) {
        this(context, null);
    }

    public LetterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LetterView);
        strokeColor = typedArray.getColor(R.styleable.LetterView_strokeColor, 0xFFC4C4C4);
        checkColor = typedArray.getColor(R.styleable.LetterView_checkedColor, 0xFFEEAD0E);
        letterDefaultColor = typedArray.getColor(R.styleable.LetterView_textDefaultColor, 0xFF404040);
        letterCheckedColor = typedArray.getColor(R.styleable.LetterView_textCheckColor, 0xFF404040);
        letterSize = typedArray.getInt(R.styleable.LetterView_textSize, 16);
        strokeWidth = typedArray.getInt(R.styleable.LetterView_strokeWidth, 2);
        typedArray.recycle();
        //初始化
        initLetters();
        initPaint();
        letter = letters.get((int) (Math.random() * 26));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2 - 3, circlePaint);
        Rect letterRect = new Rect();
        letterPaint.getTextBounds(letter, 0, 1, letterRect);
        int letterWidth  = letterRect.width();
        int letterHeight = letterRect.height();
        canvas.drawText(letter, getMeasuredWidth() / 2 - letterWidth / 2, getMeasuredHeight() / 2 + letterHeight / 2, letterPaint);
    }

    /**
     * 初始化字母
     */
    private void initLetters() {
        letters = new ArrayList<>();
        for (int i = 'a'; i <= 'z'; i++) {
            letters.add((char) i + "");
        }
    }

    /**
     * 初始化Paint
     */
    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(strokeWidth);

        letterPaint = new Paint();
        letterPaint.setAntiAlias(true);
        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setTextSize(UIUtils.dp2px(mContext, letterSize));

        if (!checked) {
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setColor(strokeColor);
            letterPaint.setColor(letterDefaultColor);
        } else if (finished) {
            circlePaint.setColor(Color.TRANSPARENT);
            letterPaint.setColor(letterFinishColor);
        } else {
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setColor(checkColor);
            letterPaint.setColor(letterCheckedColor);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (finished) break;
                checked = !checked;
                initPaint();
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置字母
     *
     * @param letter
     */
    public void setLetter(String letter) {
        this.letter = letter;
        invalidate();
    }

    /**
     * 获取字母view上的字母
     *
     * @return
     */
    public String getLetter() {
        return letter;
    }

    /**
     * 当前字母view是否被选中
     *
     * @return
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 判断是否完成
     *
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * 是否被设置了
     *
     * @param seted
     */
    public void setSeted(boolean seted) {
        this.seted = seted;
    }

    public boolean isSeted() {
        return seted;
    }

    public void finish() {
        finished = true;
        initPaint();
        invalidate();
    }


    //------------------ 设置自定义属性 ------------------------//

    /**
     * 设置文字大小
     *
     * @param letterSize
     */
    public void setLetterSize(int letterSize) {
        this.letterSize = letterSize;
        letterPaint.setTextSize(UIUtils.dp2px(mContext, letterSize));
    }

    /**
     * 设置文字颜色
     *
     * @param color
     */
    public void setLetterDefaultColor(int color) {
        this.letterDefaultColor = color;
        letterPaint.setColor(color);
    }

    /**
     * 点击之后的文字颜色
     *
     * @param color
     */
    public void setLetterCheckedColor(int color) {
        this.letterCheckedColor = color;
    }

    /**
     * 设置完成是的颜色
     *
     * @param color
     */
    public void setLetterFinishColor(int color) {
        this.letterFinishColor = color;
    }

    /**
     * 设置边框大小
     *
     * @param width
     */
    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
        circlePaint.setStrokeWidth(width);
    }

    /**
     * 设置边框颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        this.strokeColor = color;
        circlePaint.setColor(color);
    }

    /**
     * 设置点击之后的颜色
     */
    public void setCheckedColor(int checkedColor) {
        this.checkColor = checkedColor;
    }

}
