package cn.ifafu.ifafu.electricity.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.ifafu.ifafu.mvp.base.BaseApplication;

public class StringUtils {

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String imei() {
        String str;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getDeviceId() != null) {
                str = telephonyManager.getDeviceId();
            } else {
                str = Settings.Secure.getString(BaseApplication.getContext().getApplicationContext().getContentResolver(), "android_id");
            }
        } catch(Exception e) {
            str = Settings.Secure.getString(BaseApplication.getContext().getApplicationContext().getContentResolver(), "android_id");
        }
        return md5(str);
    }

    @SuppressLint("ObsoleteSdkInt")
    public static String getUserAgent() {
        String userAgent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(BaseApplication.getContext());
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

    private static String md5(String str) {
        try {
            return tc(MessageDigest.getInstance("MD5").digest(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException unused) {
            return "";
        }
    }

    private static String tc(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(255 & b);
            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

}
