package cn.ifafu.ifafu.data.entity;

import android.annotation.SuppressLint;
import android.graphics.Color;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Entity
public class SyllabusSetting {

    @Id
    private String account;

    private int weekCnt = 24; //总共周数

    private int nodeCnt = 12; //每日课程节数

    private boolean showSaturday = true; //显示周六

    private boolean showSunday = true; //显示周日

    private boolean showBeginTimeText = true; //显示侧边栏时间

    private boolean showHorizontalLine = true; //显示水平分割线

    private boolean showVerticalLine = true; //显示竖直分割线

    private String openingDay = "2019-09-01"; //开学时间

    private int nodeLength = 45; //一节课的时间

    private int firstDayOfWeek = Calendar.SUNDAY; //每周的第一天

    private String background; //课表背景

    private int textSize = 12; //课程字体大小

    private int themeColor = Color.BLACK; //主题颜色

    private boolean statusDartFont = true; //状态栏深色字体

    private boolean isForceRefresh = false; // 每次进入课表，自动刷新课表

    @Transient
    private int[] beginTime;

    public SyllabusSetting(String account) {
        this.account = account;
    }

    @Generated(hash = 665193961)
    public SyllabusSetting(String account, int weekCnt, int nodeCnt, boolean showSaturday, boolean showSunday,
            boolean showBeginTimeText, boolean showHorizontalLine, boolean showVerticalLine, String openingDay,
            int nodeLength, int firstDayOfWeek, String background, int textSize, int themeColor,
            boolean statusDartFont, boolean isForceRefresh) {
        this.account = account;
        this.weekCnt = weekCnt;
        this.nodeCnt = nodeCnt;
        this.showSaturday = showSaturday;
        this.showSunday = showSunday;
        this.showBeginTimeText = showBeginTimeText;
        this.showHorizontalLine = showHorizontalLine;
        this.showVerticalLine = showVerticalLine;
        this.openingDay = openingDay;
        this.nodeLength = nodeLength;
        this.firstDayOfWeek = firstDayOfWeek;
        this.background = background;
        this.textSize = textSize;
        this.themeColor = themeColor;
        this.statusDartFont = statusDartFont;
        this.isForceRefresh = isForceRefresh;
    }

    @Generated(hash = 310423812)
    public SyllabusSetting() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getWeekCnt() {
        return this.weekCnt;
    }

    public void setWeekCnt(int weekCnt) {
        this.weekCnt = weekCnt;
    }

    public int getTotalNode() {
        return this.nodeCnt;
    }

    public void setTotalNode(int nodeCnt) {
        this.nodeCnt = nodeCnt;
    }

    public boolean getShowSaturday() {
        return this.showSaturday;
    }

    public void setShowSaturday(boolean showSaturday) {
        this.showSaturday = showSaturday;
    }

    public boolean getShowSunday() {
        return this.showSunday;
    }

    public void setShowSunday(boolean showSunday) {
        this.showSunday = showSunday;
    }

    public int getNodeCnt() {
        return this.nodeCnt;
    }

    public void setNodeCnt(int nodeCnt) {
        this.nodeCnt = nodeCnt;
    }

    public int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public String getOpeningDay() {
        return this.openingDay;
    }

    public void setOpeningDay(String openingDay) {
        this.openingDay = openingDay;
    }

    public int[] getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int[] beginTime) {
        this.beginTime = beginTime;
    }

    @SuppressLint("DefaultLocale")
    public String[] getBeginTimeText() {
        if (beginTime == null) {
            return null;
        }
        String[] text = new String[weekCnt];
        for (int i = 0; i < text.length && i < beginTime.length; i++) {
            text[i] = String.format("%d:%02d", beginTime[i] / 100, beginTime[i] % 100);
        }
        return text;
    }

    public int getNodeLength() {
        return this.nodeLength;
    }

    public void setNodeLength(int nodeLength) {
        this.nodeLength = nodeLength;
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public boolean getShowBeginTimeText() {
        return this.showBeginTimeText;
    }

    public void setShowBeginTimeText(boolean showBeginTimeText) {
        this.showBeginTimeText = showBeginTimeText;
    }

    public boolean getShowHorizontalLine() {
        return this.showHorizontalLine;
    }

    public void setShowHorizontalLine(boolean showHorizontalLine) {
        this.showHorizontalLine = showHorizontalLine;
    }

    public boolean getShowVerticalLine() {
        return this.showVerticalLine;
    }

    public void setShowVerticalLine(boolean showVerticalLine) {
        this.showVerticalLine = showVerticalLine;
    }

    public int getCurrentWeek() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date firstStudyDate = format.parse(openingDay);
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(firstDayOfWeek);
            int currentYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(firstStudyDate);
            int firstYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            int nowWeek = currentYearWeek - firstYearWeek + 1;
            System.out.println("now week = " + nowWeek);
            return nowWeek > 0? nowWeek: -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean getIsForceRefresh() {
        return this.isForceRefresh;
    }

    public void setIsForceRefresh(boolean isForceRefresh) {
        this.isForceRefresh = isForceRefresh;
    }

    public int getThemeColor() {
        return this.themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public boolean getStatusDartFont() {
        return this.statusDartFont;
    }

    public void setStatusDartFont(boolean statusDartFont) {
        this.statusDartFont = statusDartFont;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyllabusSetting that = (SyllabusSetting) o;
        return weekCnt == that.weekCnt &&
                nodeCnt == that.nodeCnt &&
                showSaturday == that.showSaturday &&
                showSunday == that.showSunday &&
                showBeginTimeText == that.showBeginTimeText &&
                showHorizontalLine == that.showHorizontalLine &&
                showVerticalLine == that.showVerticalLine &&
                nodeLength == that.nodeLength &&
                firstDayOfWeek == that.firstDayOfWeek &&
                textSize == that.textSize &&
                themeColor == that.themeColor &&
                statusDartFont == that.statusDartFont &&
                isForceRefresh == that.isForceRefresh &&
                Objects.equals(account, that.account) &&
                Objects.equals(openingDay, that.openingDay) &&
                Objects.equals(background, that.background) &&
                Arrays.equals(beginTime, that.beginTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(account, weekCnt, nodeCnt, showSaturday, showSunday, showBeginTimeText, showHorizontalLine, showVerticalLine, openingDay, nodeLength, firstDayOfWeek, background, textSize, themeColor, statusDartFont, isForceRefresh);
        result = 31 * result + Arrays.hashCode(beginTime);
        return result;
    }
}
