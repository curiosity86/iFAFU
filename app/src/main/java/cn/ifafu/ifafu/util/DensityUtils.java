package cn.ifafu.ifafu.util;

import android.content.Context;
import android.util.TypedValue;

import cn.ifafu.ifafu.mvp.base.BaseApplication;

/**
 * create by woolsen on 19/7/24
 */
public class DensityUtils {
    /**
     * dp转换为px
     * @param context 上下文
     * @param dpValue dp
     * @return px
     */
    public static float dp2px(Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, BaseApplication.getContext().getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, float spVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转换为dp
     * @param context 上下文
     * @param pxValue px
     * @return dp
     */
    public static float px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale);
    }

    public static float px2sp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

}
