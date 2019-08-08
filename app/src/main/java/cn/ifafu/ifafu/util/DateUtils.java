package cn.ifafu.ifafu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.ifafu.ifafu.view.syllabus.data.DayOfWeek;

/**
 * create by woolsen on 19/7/16
 */
public class DateUtils {

    private static String[] weekdays = {
            "周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static String getWeekdayCN(@DayOfWeek int weekday) {
        return weekdays[weekday - 1];
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
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date date = format.parse(dateText);
            return getWeekDates(date, offsetWeek, firstDayOfWeek, dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取一周的日期
     *
     * @param date           当日日期
     * @param firstDayOfWeek {@link Calendar} 每周的首日是周几
     * @param dateFormat     {@link SimpleDateFormat}
     * @return String[]
     */
    public static String[] getWeekDates(Date date, int firstDayOfWeek, String dateFormat) {
        return getWeekDates(date, 0, firstDayOfWeek, dateFormat);
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
    public static String[] getWeekDates(Date date, int offsetWeek, int firstDayOfWeek, String dateFormat) {
        if (offsetWeek != 0) {
            date.setTime(date.getTime() + offsetWeek * 604800000L);
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.CHINA);
        String[] dates = new String[7];
        Calendar c = Calendar.getInstance();
        c.setTime(getFirstDayOfWeek(date, firstDayOfWeek));
        for (int i = 0; i < 7; i++) {
            dates[i] = format.format(c.getTime());
            c.add(Calendar.DATE, 1);
        }
        return dates;
    }

    /**
     * 获取date当周第一天的时间
     *
     * @param date           date
     * @param firstDayOfWeek {@link Calendar}
     * @return date
     */
    private static Date getFirstDayOfWeek(Date date, int firstDayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(firstDayOfWeek);
        cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

}
