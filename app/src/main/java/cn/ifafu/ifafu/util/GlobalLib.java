package cn.ifafu.ifafu.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Score;

public class GlobalLib {

    public static String trimZero(String s) {
        if (s.indexOf(".") > 0) {
            // 去掉多余的0
            s = s.replaceAll("0+?$", "");
            // 如最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }

    public static String formatFloat(float num, int digit) {
        return trimZero(String.format("%."+ digit + "f", num));
    }

    public static float getIES(List<Score> scoreList) {
        if (scoreList == null || scoreList.isEmpty()) {
            return 0F;
        }
        float totalScore = 0;
        float totalCredit = 0;
        float totalMinus = 0;
        for (Score score : scoreList) {
            if (score.getIsIESItem()) {
                totalCredit += score.getCredit();
                if (score.getScore() < 60) { //不及格
                    if (score.getMakeupScore() == -1) { //补考成绩未出，以原成绩计算
                        totalScore += score.getScore() * score.getCredit();
                    } else if (score.getMakeupScore() >= 60) { //补考成绩及格，以60分计算
                        totalScore += 60 * score.getCredit();
                    } else { //补考成绩不及格，以补考成绩计算，并以学分1:1比例扣除相应智育分
                        totalScore += score.getMakeupScore() * score.getCredit();
                        totalMinus += score.getCredit();
                    }
                } else { //及格
                    totalScore += score.getScore() * score.getCredit();
                }
            }
        }
        return totalScore / totalCredit - totalMinus;
    }

    public static String getLocalVersionName(Context context) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static int getLocalVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 9999;
        }
    }

    public static Activity getActivityFromView(View view) {
        if (null != view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
