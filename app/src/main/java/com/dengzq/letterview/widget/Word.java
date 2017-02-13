package com.dengzq.letterview.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Company: tsingning
 * Created by dengzq
 * Created time: 2017/2/6
 * Package_name: com.dengzq.dengzqtestapp.widget.LetterView
 * Description : 单词对象
 */

public class Word {

    /**
     * 单词所在的列
     */
    public int               column;
    /**
     * 单词所在的行
     */
    public int               row;
    /**
     * 开始的索引,父控件下的index
     */
    public int               startIndex;
    /**
     * 结束索引
     */
    public int               endIndex;
    /**
     * 单词长度
     */
    public int               lenghth;
    /**
     * 单词对象
     */
    public String            engWord;
    /**
     * 单词显示方向
     */
    public LetterOrientation orientation;

    /**
     * 保存字母的对象
     */
    public List<LetterView> mLetterList=new ArrayList<>();

    private boolean finished;
    /**
     * 判断单词里所有字母都被选择
     * @return
     */
    public boolean isFinish(){
        //TODO 应该判断finish
        finished=false;
        for (int i=0;i<mLetterList.size();i++){
            finished=mLetterList.get(i).isChecked();
            if (!finished) break;
        }
        return finished;
    }

    /**
     * 返回完成状态
     * @return
     */
    public boolean getFinish(){
        return finished;
    }

}
