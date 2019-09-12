package cn.ifafu.ifafu.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.ifafu.ifafu.view.syllabus.DayOfWeek;

/**
 * create by woolsen on 19/7/16
 */
public class DateUtils {

    private static String[] weekdays = {
            "周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    private static Calendar calendar = Calendar.getInstance();

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public static String getWeekdayCN(@DayOfWeek int weekday) {
        return weekdays[weekday - 1];
    }

    public static int calcLastDays(Date fromDate, Date toDate) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTime(fromDate);
        long fromDay = calendar.getTimeInMillis() / 1000 / 60 / 60 / 24;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTime(toDate);
        long toDay = calendar.getTimeInMillis() / 1000 / 60 / 60 / 24;
        return (int) (toDay - fromDay);
    }

    /**
     * 获取星期文本
     *
     * @param firstDayOfWeek {@link Calendar}
     * @param weekend        是否包含周末
     * @return String[]
     */
    public static String[] getWeekdayText(int firstDayOfWeek, boolean weekend) {
        String[] result = weekend ? new String[7] : new String[5];
        int indexOfOffset = 9 + Calendar.SUNDAY - firstDayOfWeek;
        for (int i = 0, j = 0; i < 7; i++) {
            int t = (i + indexOfOffset) % 7;
            if (weekend || t != 0 && t != 6) {
                result[j++] = weekdays[t];
            }
        }
        return result;
    }

    @SuppressLint("DefaultLocale")
    public static String calcIntervalTime(long startMill, long endMill) {
        long sec = (endMill - startMill) / 1000;
        if (sec < 0) {
            return "";
        }
        if (sec < 60) {
            return String.format("%d秒", sec);
        }
        long min = sec / 60;
        if (min < 60) {
            return String.format("%d分钟", min);
        }
        long hour = min / 60;
        if (hour < 24) {
            if (min % 60 != 0) {
                return String.format("%d小时%d分钟", hour, min % 60);
            } else {
                return String.format("%d小时", hour);
            }
        }
        long day = hour / 24;
        if (day < 7) {
            if (hour % 24 != 0) {
                return String.format("%d天%d小时", day, hour % 24);
            } else {
                return String.format("%d天", day);
            }
        }
        return String.format("%d天", day);
    }

    /**
     * 获取一周的日期
     *
     * @param dateText       当日日期 example: 2019-09-01
     * @param firstDayOfWeek {@link Calendar} 每周的首日是周几
     * @param dateFormat     {@link SimpleDateFormat}
     * @return String[]
     */
    public static String[] getWeekDates(String dateText, int firstDayOfWeek, String dateFormat) {
        return getWeekDates(dateText, 0, firstDayOfWeek, dateFormat);
    }

    /**
     * 获取一周的日期
     *
     * @param dateText       当日日期 example: 2019-09-01
     * @param offsetWeek     之后第几周的日期
     * @param firstDayOfWeek {@link Calendar} 每周的首日是周几
     * @param dateFormat     {@link SimpleDateFormat}
     * @return String[]
     */
    public static String[] getWeekDates(String dateText, int offsetWeek, int firstDayOfWeek, String dateFormat) {
        try {
            Date date = format.parse(dateText);
            return getWeekDates(date, offsetWeek, firstDayOfWeek, dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see #getWeekDates(Date, int, int, String)
     */
    public static String[] getWeekDates(Date date, int firstDayOfWeek, String dateFormat) {
        return getWeekDates(date, 0, firstDayOfWeek, dateFormat);
    }

    /**
     *
     * @param text example: 2019-01-01
     * @throws ParseException
     */
    public static int getMonth(String text) throws ParseException {
        Date date = format.parse(text);
        return getMonth(date);
    }

    public static int getMonth(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取一周的日期
     *
     * @param date           当日日期
     * @param offsetWeek     之后第几周的日期
     * @param firstDayOfWeek {@link Calendar} 每周的首日是周几
     * @param dateFormat     {@link SimpleDateFormat}
     * @return String[]
     */
    private static String[] getWeekDates(Date date, int offsetWeek, int firstDayOfWeek, String dateFormat) {
        if (offsetWeek != 0) {
            date.setTime(date.getTime() + offsetWeek * 604800000L);
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.CHINA);
        String[] dates = new String[7];
        calendar.setTime(getFirstDayOfWeek(date, firstDayOfWeek));
        for (int i = 0; i < 7; i++) {
            dates[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    /**
     * 获取date周第一天的时间
     *
     * @param date           date
     * @param firstDayOfWeek {@link Calendar}
     * @return date
     */
    private static Date getFirstDayOfWeek(Date date, int firstDayOfWeek) {
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前星期
     *
     * @return {@link Calendar}
     */
    public static int getCurrentWeekday() {
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当前周
     *
     * @param firstStudyDate 开学日期
     * @param date           今日日期
     * @param firstDayOfWeek 每周第一天是星期几
     * @return 当前周
     */
    public static int getCurrentWeek(Date firstStudyDate, Date date, @DayOfWeek int firstDayOfWeek) {
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        int currentYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(firstStudyDate);
        int firstYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (currentYearWeek < firstYearWeek) {
            firstYearWeek -= 52;
        }
        return currentYearWeek - firstYearWeek + 1;
    }

    /**
     * @see #getCurrentWeek(Date, Date, int)
     */
    public static int getCurrentWeek(Date firstStudyDate, @DayOfWeek int firstDayOfWeek) {
        return getCurrentWeek(firstStudyDate, new Date(), firstDayOfWeek);
    }

}
