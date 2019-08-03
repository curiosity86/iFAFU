package cn.ifafu.ifafu.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GlobalLib {

    public static String[] dateUnit = new String[] {"年", "月", "天", "小时", "分钟", "秒"};

    public static Long[] dateUnitDistance = new Long[] {12L , 30L , 24L, 60L, 60L};

    public static String[] weekDayName = new String[] {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static float GetRawSize(Context context, int unit, float value) {
        Resources res = context.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

    public static boolean CompareUtfWithGbk(String utf8, String gbk) throws IOException {
        return URLEncoder.encode(gbk, "gbk").contains(URLEncoder.encode(utf8, "gbk"));
    }

    public static BitmapDrawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static String[] CompareWithNowTime(List<Integer> compareTime) {
        String[] answer = new String[] {"-", "0", "分钟"};

        DateFormat    dateFormat = new SimpleDateFormat(
                "yyyy.MM.dd.HH.mm", Locale.getDefault());
        String        dateString = dateFormat.format(new Date(System.currentTimeMillis()));
        String[]      systemTime = dateString.split("\\.");
        for (int i = 0; i < compareTime.size(); i++) {
            int compare = compareTime.get(i);
            int system  = Integer.parseInt(systemTime[i]);
            if (compare < system) {
                answer[0] = "-";
                answer[1] = String.valueOf(system - compare);
                answer[2] = dateUnit[i];
                break;
            } else if (compare > system) {
                answer[0] = "+";
                answer[1] = String.valueOf(compare - system);
                answer[2] = dateUnit[i];
                break;
            }
        }

        return answer;
    }

    public static String GetLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static int GetLocalVersionCode(Context ctx) {
        int versionCode = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void PutTextToClipboard(Context ctx, String label, String text) {
        ClipboardManager clipboardManager =
                (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static int GetNowWeek(String firstWeek, long currentTime) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            /*
            cal.setTime(simpleDateFormat.parse(firstWeek));
            int first = cal.get(Calendar.WEEK_OF_YEAR);
            cal.setTime(new Date(currentTime));
            int now   = cal.get(Calendar.WEEK_OF_YEAR);
            return now - first + 1;
            */
            long first = simpleDateFormat.parse(firstWeek).getTime();
            return (int)((currentTime - first)/(1000*3600*24))/7 + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getTimeString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public static long stringToTimestamp(String time, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            return dateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String[] getStudyTime(String firstWeek, Long currentTime) {
        String[] answer = new String[3];
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "MM月dd日", Locale.getDefault());

        Date now = new Date(currentTime);
        cal.setTime(now);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        int nowWeek = GetNowWeek(firstWeek, currentTime);
        String nowWeekString = "放假中";
        if (nowWeek > 0 && nowWeek <= 24) {
            nowWeekString = String.format(
                    Locale.getDefault(), "第%d周", nowWeek);
        }
        answer[0] = String.format(
                Locale.getDefault(),
                "%s %s %s",
                simpleDateFormat.format(now),
                weekDayName[weekDay - 1],
                nowWeekString);
        answer[1] = String.valueOf(nowWeek);
        answer[2] = String.valueOf(weekDay - 1);

        return answer;
    }

    public static String GetRuntime(long beginning) {
        long now = System.currentTimeMillis() / 1000;
        long distance = now - beginning;

        Long[] time = new Long[] {0L, 0L, 0L, 0L, 0L, 0L};
        time[time.length - 1] = distance;
        for (int i = dateUnitDistance.length - 1; i >= 0; i--) {
            if (time[i + 1] > dateUnitDistance[i]) {
                time[i] = time[i + 1] / dateUnitDistance[i];
                time[i + 1] = time[i + 1] % dateUnitDistance[i];
            } else {
                break;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < time.length; i++) {
            if (time[i] != 0L) {
                stringBuilder.append(time[i]);
                stringBuilder.append(dateUnit[i]);
            }
        }
        return stringBuilder.toString();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
