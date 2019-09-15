package cn.ifafu.ifafu.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;

import cn.ifafu.ifafu.mvp.base.BaseApplication;

//跟App相关的辅助类
public class AppUtils {

    public static synchronized Object getMetaValue(Context context, String key) {
        try {
            return context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData
                    .getInt(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            int labelRes = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .applicationInfo
                    .labelRes;
            return context.getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized int getVersionCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取图标 bitmap
     *
     * @param context
     */
    public static synchronized Drawable getIconDrawable(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext()
                    .getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
            return packageManager.getApplicationIcon(applicationInfo);
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }


    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String imei() {
        String str;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getDeviceId() != null) {
                str = telephonyManager.getDeviceId();
            } else {
                str = Settings.Secure.getString(BaseApplication.getAppContext().getApplicationContext().getContentResolver(), "android_id");
            }
        } catch(Exception e) {
            str = Settings.Secure.getString(BaseApplication.getAppContext().getApplicationContext().getContentResolver(), "android_id");
        }
        return StringUtils.md5(str);
    }

    @SuppressLint("ObsoleteSdkInt")
    public static String getUserAgent() {
        String userAgent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(BaseApplication.getAppContext());
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        if (userAgent == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}