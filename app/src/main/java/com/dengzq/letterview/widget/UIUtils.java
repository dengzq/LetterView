package com.dengzq.letterview.widget;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Company: tsingning
 * Created by dengzq
 * Created time: 2017/2/4
 * Package_name: com.dengzq.dengzqtestapp.utils
 * Description : ui工具类
 */

public class UIUtils {

    /**
     * px转成dp
     * @param context
     * @param px
     * @return
     */
    public static int px2dp(Context context,float px){
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        return (int) (px/displayMetrics.density+0.5);
    }

    /**
     * dp转成px
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context,float dp){
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        return (int) (dp*displayMetrics.density+0.5);
    }

    /**
     * px转成sp
     * @param context
     * @param px
     * @return
     */
    public static int px2sp(Context context,float px){
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        return (int) (px/displayMetrics.scaledDensity+0.5);
    }

    /**
     * sp转成px
     * @param context
     * @param sp
     * @return
     */
    public static int sp2px(Context context,float sp){
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        return (int) (sp*displayMetrics.scaledDensity+0.5);
    }

}
