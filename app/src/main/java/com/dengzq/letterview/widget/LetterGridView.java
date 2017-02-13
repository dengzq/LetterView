package com.dengzq.letterview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.GridLayout;

import com.dengzq.letterview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Company: tsingning
 * Created by dengzq
 * Created time: 2017/2/5
 * Package_name: com.dengzq.dengzqtestapp.widget.LetterView
 * Description : 字母View网格式布局
 */

public class LetterGridView extends GridLayout {
    private static final String  TAG            = "LetterGridView";
    private static final int     LAYOUT_PADDING = 10;     //给控件留出的左右间距
    private              boolean added          = false;  //判断是否添加了letterView
    private Paint   mCorrnerPaint;                        //边框画笔
    private Context mContext;                             //上下文
    private int     letterColumn;                         //字母列数
    private int     letterRow;                            //字母行数
    private int     letterHorizontalMargin;               //字母水平间距
    private int     letterVerticalMargin;                 //字母竖直间距
    private int     letterWidth;                          //字母半径
    private int     letterSize;                           //字母文本大小
    private int     letterDefaultColor;                   //默认文本颜色
    private int     letterCheckColor;                     //选中文本颜色
    private int     letterFinishColor;                    //完成文本颜色
    private int     strokeWidth;                          //边框大小
    private int     strokeColor;                          //边框颜色
    private int     checkedColor;                         //选中颜色
    private int     strokeFinishWidth;                    //完成边框宽度
    private int     strokeFinishColor;                    //完成边框颜色
    private int     mRange;                               //遍历的区域
    private int     mCheckRange;                          //已经遍历的区域
    private boolean invalidHorizontal;                    //行遍历是否合法
    private boolean invalidVertical;                      //列遍历是否合法
    private List<String> words = new ArrayList<>();       //保存单词
    private RectF fstRectF;                               //第一个矩形
    private RectF secRectF;                               //第二个矩形
    private Path  fstPath;                                //第一条路径
    private Path  secPath;                                //第二条路径
    private List<Word>          mCurrentWords      = new ArrayList<>();
    private List<LetterView>    mStartViews        = new ArrayList<>();
    private List<LetterView>    mEndViews          = new ArrayList<>();
    private List<Word>          mWordlist          = new ArrayList<>();
    private SparseArray<Word>   mSetedIndexArray   = new SparseArray<>();
    private SparseArray<String> sparseCheckedArray = new SparseArray<>();

    private LetterViewOnClickListener mLetterViewOnClickListener;

    public LetterGridView(Context context) {
        this(context, null);
    }

    public LetterGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LetterGridView);
        letterColumn = typedArray.getInt(R.styleable.LetterGridView_letterColumn, 5);
        letterRow = typedArray.getInt(R.styleable.LetterGridView_letterRow, 5);
        letterHorizontalMargin = typedArray.getInt(R.styleable.LetterGridView_letterHorizontalMargin, 2);
        letterVerticalMargin = typedArray.getInt(R.styleable.LetterGridView_letterVerticalMargin, 2);
        letterWidth = typedArray.getInt(R.styleable.LetterGridView_letterViewSize, 30);
        letterSize = typedArray.getInt(R.styleable.LetterGridView_textSize, 16);
        letterDefaultColor = typedArray.getColor(R.styleable.LetterGridView_textDefaultColor, 0xFF404040);
        letterCheckColor = typedArray.getColor(R.styleable.LetterGridView_textCheckColor, 0xFF404040);
        letterFinishColor = typedArray.getColor(R.styleable.LetterGridView_textFinishColor, 0xFF404040);
        strokeWidth = typedArray.getInt(R.styleable.LetterGridView_strokeWidth, 2);
        strokeColor = typedArray.getInt(R.styleable.LetterGridView_strokeColor, 0xFFC4C4C4);
        checkedColor = typedArray.getColor(R.styleable.LetterGridView_checkedColor, 0xFFEEAD0E);
        strokeFinishColor = typedArray.getColor(R.styleable.LetterGridView_strokeFinishColor, 0xFFEEB422);
        strokeFinishWidth = typedArray.getInt(R.styleable.LetterGridView_strokeFinishWidth, 2);
        typedArray.recycle();

        mContext = context;
        mLetterViewOnClickListener = new LetterViewOnClickListener();
        //初始化路径和矩形
        fstRectF = new RectF();
        secRectF = new RectF();
        fstPath = new Path();
        secPath = new Path();
        //初始化边框画笔
        mCorrnerPaint = new Paint();
        mCorrnerPaint.setAntiAlias(true);
        mCorrnerPaint.setStyle(Paint.Style.STROKE);
        mCorrnerPaint.setColor(strokeFinishColor);
        mCorrnerPaint.setStrokeWidth(UIUtils.dp2px(context, strokeFinishWidth));

        //强制走onDraw()
        setWillNotDraw(false);
        setPadding(LAYOUT_PADDING / 2, LAYOUT_PADDING / 2, LAYOUT_PADDING / 2, LAYOUT_PADDING / 2);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int widthSize  = MeasureSpec.getSize(widthSpec);
        widthMode  = MeasureSpec.getMode(widthSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);
         heightMode = MeasureSpec.getMode(heightSpec);

        addletterView(widthSize,heightSize);
    }
    int widthMode;
    int heightMode;

    private void addletterView(int widthSize, int heightSize) {
        if (added || widthSize < 1 || heightSize < 1) return;



        int            maxWidth       = (widthSize - LAYOUT_PADDING - (letterColumn - 1) * letterHorizontalMargin) / letterColumn;
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float          desity         = displayMetrics.density;
        if (desity * letterWidth * 1.0f >= maxWidth * 1.0f) {
            letterColumn--;
            if (letterColumn > 0)
                addletterView(widthSize, heightSize);
        } else {
            int maxHeight = (heightSize - LAYOUT_PADDING - (letterRow - 1) * letterVerticalMargin) / letterRow;
            if (desity * letterWidth * 1.0f >= maxHeight * 1.0f) {
                letterRow--;
                if (letterRow > 0)
                    addletterView(widthSize, heightSize);
            } else {
                Log.d(TAG, "宽度模式=>"+widthMode+" 大小=>"+widthSize);
                Log.d(TAG, "高度模式=>"+heightMode+" 大小=>"+heightSize);

                added = !added;
                setColumnCount(letterColumn);
                setRowCount(letterRow);
                int width = Math.min(Math.min(maxHeight, maxWidth), letterWidth);

                Log.d(TAG, "直径: " + letterWidth * desity);
                Log.d(TAG, "max宽: " + maxWidth);
                Log.d(TAG, "max高: " + maxHeight);
                Log.d(TAG, "最后取值: " + width);
                Log.d(TAG, "列column: " + letterColumn);
                Log.d(TAG, "行row: " + letterRow);

                for (int i = 0; i < letterColumn; i++) {
                    for (int j = 0; j < letterRow; j++) {
                        LetterView   letterView = new LetterView(mContext);
                        LayoutParams params     = new LayoutParams();
                        params.width = UIUtils.dp2px(mContext, width);
                        params.height = UIUtils.dp2px(mContext, width);
                        if (i / letterColumn != 1)
                            params.rightMargin = letterHorizontalMargin;
                        if (j / letterRow != 1)
                            params.bottomMargin = letterVerticalMargin;
                        letterView.setLayoutParams(params);
                        letterView.setOnClickListener(mLetterViewOnClickListener);
                        //设置letterView的属性
                        letterView.setCheckedColor(checkedColor);
                        letterView.setLetterDefaultColor(letterDefaultColor);
                        letterView.setLetterCheckedColor(letterCheckColor);
                        letterView.setLetterFinishColor(letterFinishColor);
                        letterView.setLetterSize(letterSize);
                        letterView.setStrokeWidth(strokeWidth);
                        letterView.setStrokeColor(strokeColor);

                        addView(letterView);
                    }
                }
                //添加单词
                addLetters();
            }

        }


    }

    private void addLetters() {
        if (words.size() < 1) return;
        LetterOrientation orientation;
        for (int i = 0; i < words.size(); i++) {
            orientation = obtainOrientation();
            initRange();
            initStatus();
            if (orientation == LetterOrientation.HORIZONTAL) {
                addHorizontalLetter(words.get(i));
            } else {
                addVerticalLetter(words.get(i));
            }
        }
    }

    /**
     * 设置水平方向单词
     *
     * @param word
     */
    private void addHorizontalLetter(String word) {
        Log.d(TAG, "水平方向单词: " + word);
        if (mCheckRange >= mRange) {
            invalidHorizontal = true;
            if (!invalidVertical) {
                initRange();
                addVerticalLetter(word);
            }
            return;
        }
        int range = letterColumn - word.length() + 1; //水平方向选择范围
        mRange = range * letterRow;
        if (range <= 0) {
            invalidHorizontal = true;
            if (!invalidVertical) {
                initRange();
                addVerticalLetter(word);
            } else {
                Log.i(TAG, "addHorizontalLetter: English word is longer than the table!");
            }
        } else {

            int defineRow    = (int) (Math.random() * letterRow);
            int defineColumn = (int) (Math.random() * range);
            int index;
            if (defineRow == 0) index = defineColumn;
            else {
                if (defineColumn == 0) {
                    index = defineRow * letterColumn;
                } else {
                    index = defineColumn + defineRow * letterColumn;
                }
            }
            boolean valid = checkRangeValid(word, index, LetterOrientation.HORIZONTAL);
            if (!valid) {
                //当前选择的位置不合法，保存已经处理过的index，继续遍历
                if (sparseCheckedArray.get(index) == null) {
                    sparseCheckedArray.put(index, "checked");
                    mCheckRange++;
                }
                addHorizontalLetter(word);
            } else {
                //合法,设置单词，保存已经设置的index
                setLetter(word, index, LetterOrientation.HORIZONTAL);
            }
        }
    }

    /**
     * 添加竖直方向上的单词
     *
     * @param word
     */
    private void addVerticalLetter(String word) {
        Log.d(TAG, "竖直方向单词: " + word);
        if (mCheckRange >= mRange) {
            invalidVertical = true;
            if (!invalidHorizontal) {
                initRange();
                addHorizontalLetter(word);
            }
            return;
        }
        int range = letterRow - word.length() + 1;
        mRange = range * letterColumn;
        if (range <= 0) {
            invalidVertical = true;
            if (!invalidHorizontal) {
                initRange();
                addHorizontalLetter(word);
            } else {
                Log.i(TAG, "addVerticalLetter: English word is longer than the table!");
            }
        } else {

            int defineRow    = (int) (Math.random() * range);
            int defineColumn = (int) (Math.random() * letterColumn);
            int index        = 0;
            if (defineRow == 0) index = defineColumn;
            else {
                if (defineColumn == 0) {
                    index = defineRow * letterColumn;
                } else {
                    index = defineColumn + defineRow * letterColumn;
                }
            }
            boolean valid = checkRangeValid(word, index, LetterOrientation.VERTICAL);
            if (!valid) {
                if (sparseCheckedArray.get(index) == null) {
                    sparseCheckedArray.put(index, "checked");
                    mCheckRange++;
                }
                addVerticalLetter(word);
            } else {
                setLetter(word, index, LetterOrientation.VERTICAL);
            }
        }
    }

    /**
     * 设置
     *
     * @param engWord
     * @param index
     * @param orientation
     */
    private void setLetter(String engWord, int index, LetterOrientation orientation) {
        if (TextUtils.isEmpty(engWord)) return;
        if (!validIndex(index)) return;
        initStatus();
        int  startIndex = index;
        int  endIndex   = 0;
        Word word       = new Word();
        if (orientation == LetterOrientation.HORIZONTAL) {
            for (int i = 0; i < engWord.length(); i++) {
                if (!validIndex(index)) break;
                LetterView letterView = (LetterView) getChildAt(index);
                letterView.setLetter(String.valueOf(engWord.charAt(i)));
                letterView.setSeted(true);
                word.mLetterList.add(letterView);
                if (i == engWord.length() - 1) endIndex = index;
                index++;
            }
        } else {
            for (int i = 0; i < engWord.length(); i++) {
                if (!validIndex(index)) break;
                LetterView letterView = (LetterView) getChildAt(index);
                letterView.setLetter(String.valueOf(engWord.charAt(i)));
                letterView.setSeted(true);
                word.mLetterList.add(letterView);
                if (i == engWord.length() - 1) endIndex = index;
                index += letterColumn;
            }
        }
        word.startIndex = startIndex;
        word.endIndex = endIndex;
        word.orientation = orientation;
        word.engWord = engWord;
        word.lenghth = engWord.length();
        //保存单词对象，
        //保存设置了单词的index,多个index对应一个单词
        mWordlist.add(word);
        if (orientation == LetterOrientation.HORIZONTAL) {
            for (int i = startIndex; i <= endIndex; i++) {
                mSetedIndexArray.put(i, word);
            }
        } else {
            for (int i = startIndex; i <= endIndex; ) {
                mSetedIndexArray.put(i, word);
                i += letterColumn;
            }
        }

    }

    /**
     * 检测单词设置的范围是否合法
     *
     * @param word
     * @param index
     * @param orientation
     * @return
     */
    private boolean checkRangeValid(String word, int index, LetterOrientation orientation) {

        boolean valid       = false;
        int     checkLength = word.length();
        if (orientation == LetterOrientation.HORIZONTAL) {
            int i = 0;
            while (i < checkLength) {
                if (!validIndex(index)) {
                    valid = false;
                    break;
                }
                LetterView letterView = (LetterView) getChildAt(index);
                //如果设置了，当前选择的开始位置不合法
                valid = !letterView.isSeted();
                if (!valid) break;
                else {
                    index++;
                    i++;
                }
            }
        } else {
            int i = 0;
            while (i < checkLength) {
                if (!validIndex(index)) {
                    valid = false;
                    break;
                }
                LetterView letterView = (LetterView) getChildAt(index);
                valid = !letterView.isSeted();
                if (!valid) break;
                else {
                    index += letterColumn;
                    i++;
                }
            }
        }
        return valid;
    }

    /**
     * 判断index
     *
     * @param index
     * @return
     */
    private boolean validIndex(int index) {
        return index < letterColumn * letterRow && index >= 0;
    }

    /**
     * 遍历方向改变，重置范围
     */
    private void initRange() {
        mCheckRange = 0;
        mRange = 1;
        sparseCheckedArray.clear();
    }

    /**
     * 重置状态
     */
    private void initStatus() {
        invalidHorizontal = false;
        invalidVertical = false;
    }

    /**
     * 判断单词两端是否没有单词并没被选中
     * 或者选中但属于合法范围
     * @param orientation
     * @param startIndex
     * @param endIndex
     * @return true 非法; false 合法
     */
    private boolean isValidForeAndBack(LetterOrientation orientation, int startIndex, int endIndex) {
        boolean checkStart;
        boolean checkEnd;
        int     start;
        int     end;
        if ((mSetedIndexArray.get(startIndex).lenghth == letterColumn && orientation == LetterOrientation.HORIZONTAL) ||
                (mSetedIndexArray.get(startIndex).lenghth == letterRow && orientation == LetterOrientation.VERTICAL))
            return true;
        if (orientation == LetterOrientation.HORIZONTAL) {
            start = startIndex - 1;
            end = endIndex + 1;
        } else {
            start = startIndex - letterColumn;
            end = endIndex + letterColumn;
        }
        //检测前面一个字母
        if (validIndex(start)) {
            boolean check = ((LetterView) getChildAt(start)).isChecked();
            if (check) {
                Word word = mSetedIndexArray.get(start);
                if (orientation == LetterOrientation.VERTICAL)
                    checkStart = word != null && word.getFinish();
                else {
                    if (startIndex % letterColumn != 0)
                        checkStart = word != null && word.getFinish();
                    else checkStart = true;
                }
            } else checkStart = true;
        } else checkStart = true;

        //检测后面一个字母
        if (validIndex(end)) {
            boolean check = ((LetterView) getChildAt(end)).isChecked();
            if (check) {
                Word word = mSetedIndexArray.get(end);
                if (orientation == LetterOrientation.VERTICAL)
                    checkEnd = word != null && word.getFinish();
                else {
                    if (end % letterColumn != 0)
                        checkEnd = word != null && word.getFinish();
                    else checkEnd = true;
                }
            } else checkEnd = true;
        } else checkEnd = true;
        return checkStart && checkEnd;
    }

    private LetterOrientation obtainOrientation() {
        return Math.random() * 2 >= 1 ? LetterOrientation.HORIZONTAL : LetterOrientation.VERTICAL;
    }

    /**
     * 检测当前索引所在的单词的完成状态
     *
     * @param index
     */
    private void checkWordFinishEvent(int index) {
        Word word = mSetedIndexArray.get(index);
        if (word != null) {
            boolean valid = isValidForeAndBack(word.orientation, word.startIndex, word.endIndex);
            if (word.isFinish() && valid) {
                if (word.orientation == LetterOrientation.HORIZONTAL) {
                    for (int i = word.startIndex; i <= word.endIndex; i++) {
                        ((LetterView) getChildAt(i)).finish();
                    }
                } else {
                    for (int i = word.startIndex; i <= word.endIndex; ) {
                        ((LetterView) getChildAt(i)).finish();
                        i += letterColumn;
                    }
                }
                //保存信息
                mCurrentWords.add(word);
                mStartViews.add((LetterView) getChildAt(word.startIndex));
                mEndViews.add((LetterView) getChildAt(word.endIndex));
                invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentWords != null && mCurrentWords.size() > 0) {
            for (int i = 0; i < mCurrentWords.size(); i++) {
                LetterView startChild  = mStartViews.get(i);
                LetterView endChild    = mEndViews.get(i);
                Word       currentWord = mCurrentWords.get(i);

                //开始弧形
                int centerX = startChild.getLeft() + startChild.getWidth() / 2;
                int centerY = startChild.getTop() + startChild.getHeight() / 2;
                int radius  = Math.min(startChild.getWidth(), startChild.getHeight()) / 2;
                int left    = centerX - radius;
                int right   = centerX + radius;
                int top     = centerY - radius;
                int bottom  = centerY + radius;

                fstRectF.left = centerX - radius;
                fstRectF.right = centerX + radius;
                fstRectF.top = centerY - radius;
                fstRectF.bottom = centerY + radius;

                //结束弧形
                int endCenterX = endChild.getLeft() + endChild.getWidth() / 2;
                int endCenterY = endChild.getTop() + endChild.getHeight() / 2;
                secRectF.left = endCenterX - radius;
                secRectF.right = endCenterX + radius;
                secRectF.top = endCenterY - radius;
                secRectF.bottom = endCenterY + radius;


                if (currentWord.orientation == LetterOrientation.HORIZONTAL) {
                    //绘制左边弧形
                    canvas.drawArc(fstRectF, 90, 180, false, mCorrnerPaint);
                    //绘制右边弧形
                    canvas.drawArc(secRectF, -90, 180, false, mCorrnerPaint);
                    //绘制上部分
                    fstPath.moveTo(centerX, top);
                    fstPath.lineTo(endChild.getLeft() + radius, top);
                    canvas.drawPath(fstPath, mCorrnerPaint);
                    //绘制下部分
                    secPath.moveTo(centerX, bottom);
                    secPath.lineTo(endChild.getLeft() + radius, bottom);
                    canvas.drawPath(secPath, mCorrnerPaint);
                } else {
                    //绘制上边弧形
                    canvas.drawArc(fstRectF, -180, 180, false, mCorrnerPaint);
                    //绘制下边弧形
                    canvas.drawArc(secRectF, 0, 180, false, mCorrnerPaint);
                    //绘制左部分
                    fstPath.moveTo(left, centerY);
                    fstPath.lineTo(left, endCenterY);
                    canvas.drawPath(fstPath, mCorrnerPaint);
                    //绘制右边部分
                    secPath.moveTo(right, centerY);
                    secPath.lineTo(right, endCenterY);
                    canvas.drawPath(secPath, mCorrnerPaint);
                }
            }
        }
    }

    //------------------------ 提供给外界的方法  ------------------------------//

    /**
     * 设置单词
     *
     * @param word
     */
    public void setWords(String word) {
        if (!TextUtils.isEmpty(word)) {
            words.add(word);
        }
    }

    //----------------------  letterView点击事件 ----------------------------//
    public class LetterViewOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            int        index      = indexOfChild(view);
            LetterView letterView = (LetterView) getChildAt(index);
            if (letterView.isChecked()) {
                checkWordFinishEvent(index);
            } else {
                //当前非check状态，检测四周四个点是否完成
                if (validIndex(index - 1))
                    checkWordFinishEvent(index - 1);
                if (validIndex(index + 1))
                    checkWordFinishEvent(index + 1);
                if (validIndex(index + letterColumn))
                    checkWordFinishEvent(index + letterColumn);
                if (validIndex(index - letterColumn))
                    checkWordFinishEvent(index - letterColumn);
            }
        }
    }
}
