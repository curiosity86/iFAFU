package cn.ifafu.ifafu.util;

import android.content.Context;
import android.util.TypedValue;

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
    public static int dp2px(Context context, float dpValue) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics()) + 0.5);
    }

    public static int sp2px(Context context, float spVal) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics()) + 0.5);
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
