package cn.woolsen.android.uitl;

import android.content.Context;
import android.widget.Toast;

import cn.woolsen.android.mvp.BaseApplication;

/**
 * create by woolsen on 19/7/12
 */
public class ToastUtil {

    public static void showToastLong(String msg) {
        showToast(BaseApplication.getContext(), msg, Toast.LENGTH_LONG);
    }

    public static void showToastLong(int msg) {
        showToast(BaseApplication.getContext(), "" + msg, Toast.LENGTH_LONG);
    }

    public static void showToastShort(String msg) {
        showToast(BaseApplication.getContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(int msg) {
        showToast(BaseApplication.getContext(), "" + msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
    }
}
