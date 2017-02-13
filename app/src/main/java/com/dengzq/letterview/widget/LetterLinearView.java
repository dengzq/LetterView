package com.dengzq.letterview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.List;

/**
 * Company: tsingning
 * Created by dengzq
 * Created time: 2017/2/4
 * Package_name: com.dengzq.dengzqtestapp.widget.LetterView
 * Description : linear类型的字母view,测试用;
 */

public class LetterLinearView extends LinearLayout {
    private static final String       TAG         = "LetterLinearView";
    private static       int          PADDING     = 10;
    private              List<Letter> letterViews = new ArrayList<>();
    private              List<String> letters     = new ArrayList<>();
    private boolean added;              //是否添加过letterView，防止多次添加
    private Context mContext;           //上下文
    private Paint   mCorrnerPaint;      //绘制边框的画笔
    private View    startChild;         //边框开始的子view
    private View    endChild;           //边框结束的子view
    private boolean finish = false;     //是否已经结束

    public LetterLinearView(Context context) {
        super(context);
        init(context);
    }

    public LetterLinearView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LetterLinearView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);

        mCorrnerPaint = new Paint();
        mCorrnerPaint.setAntiAlias(true);
        mCorrnerPaint.setStyle(Paint.Style.STROKE);
        mCorrnerPaint.setColor(Color.parseColor("#EEB422"));
        mCorrnerPaint.setStrokeWidth(UIUtils.dp2px(context, 3));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode  = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize  = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        addLetterView(widthSize);
    }

    private void addLetterView(int widthSize) {
        if (added || widthSize < 1) return;
        added = !added;
        int childeCount = (int) Math.floor((widthSize - PADDING) * 1d / UIUtils.dp2px(mContext, 43));
        for (int i = 0; i < childeCount; i++) {
            LetterView                letterView = new LetterView(mContext);
            LayoutParams param      = new LayoutParams(UIUtils.dp2px(mContext, 40), UIUtils.dp2px(mContext, 40));
            if (i < childeCount - 1)
                param.rightMargin = UIUtils.dp2px(mContext, 3);
            letterView.setLayoutParams(param);
            addView(letterView);
        }
        //添加需要的单词
        int range = childeCount - letters.size() + 1;
        int startIndex;
        if (range > 0) startIndex = (int) (Math.random() * range);
        else return;
        for (int i = 0; i < letters.size(); i++) {
            LetterView letterView = (LetterView) getChildAt(startIndex);
            letterView.setLetter(letters.get(i));
            //保存包含字母的view
            Letter letter = new Letter();
            letter.index = startIndex;
            letter.letterView = letterView;
            letterViews.add(letter);
            startIndex++;
        }
    }

    public void setLetters(@NonNull String word) {
        if (TextUtils.isEmpty(word))
            return;
        int i = 0;
        while (i < word.length()) {
            letters.add(String.valueOf(word.charAt(i)));
            i++;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //选中了所有的字母，应该置为完成状态
                for (int i = 0; i < letterViews.size(); i++) {
                    finish = letterViews.get(i).letterView.isChecked();
                    if (!finish) break;
                }
                if (finish && isNonCheckedForeAndBack()) {
                    //此时所有字母都已经被选择完成
                    for (int i = 0; i < letterViews.size(); i++) {
                        letterViews.get(i).letterView.finish();
                    }
                    //绘制选中区域的边框
                    startChild = getChildAt(letterViews.get(0).index);
                    endChild = getChildAt(letterViews.get(letterViews.size() - 1).index);
                    invalidate();
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (finish) {
            int centerX = startChild.getLeft() + startChild.getWidth() / 2;
            int centerY = startChild.getTop() + startChild.getHeight() / 2;
            int radius  = Math.min(startChild.getWidth() / 2, startChild.getHeight() / 2);
            int left    = centerX - radius;
            int right   = centerX + radius;
            int top     = centerY - radius;
            int bottom  = centerY + radius;

            //绘制左边弧形
            RectF startRectF = new RectF(new Rect(left, top, right, bottom));
            canvas.drawArc(startRectF, 90, 180, false, mCorrnerPaint);
            //绘制上部分
            Path tPath = new Path();
            tPath.moveTo(centerX, top);
            tPath.lineTo(endChild.getLeft() + radius, top);
            canvas.drawPath(tPath, mCorrnerPaint);
            //绘制下部分
            Path bPath = new Path();
            bPath.moveTo(centerX, bottom);
            bPath.lineTo(endChild.getLeft() + radius, bottom);
            canvas.drawPath(bPath, mCorrnerPaint);
            //绘制右边弧形
            int   endCenterX = endChild.getLeft() + endChild.getWidth() / 2;
            int   endCenterY = endChild.getTop() + endChild.getHeight() / 2;
            RectF endRectF   = new RectF(new Rect(endCenterX - radius, endCenterY - radius, endCenterX + radius, endCenterY + radius));
            canvas.drawArc(endRectF, -90, 180, false, mCorrnerPaint);
        }
    }

    /**
     * 判断左右两边的letterView没有checked
     *
     * @return
     */
    private boolean isNonCheckedForeAndBack() {
        boolean foreCheck = letterViews.get(0).index <= 0 || !((LetterView) getChildAt(letterViews.get(0).index - 1)).isChecked();
        boolean backCheck = letterViews.get(letterViews.size() - 1).index >= getChildCount() - 1 || !((LetterView) getChildAt(letterViews.get(letterViews.size() - 1).index + 1)).isChecked();
        return foreCheck && backCheck;
    }
}
