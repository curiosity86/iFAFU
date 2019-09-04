package cn.ifafu.ifafu.data.entity;

import android.annotation.SuppressLint;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Calendar;

@Entity
public class SyllabusSetting {

    @Id
    private String account;

    private int weekCnt = 24; //总共周数

    private int nodeCnt = 12; //每日课程节数

    private boolean showSaturday = true; //显示周六

    private boolean showSunday = true; //显示周日

    private String openingDay = "2019-09-01";

    private int nodeLength = 45;

    private int firstDayOfWeek = Calendar.SUNDAY;

    private String background;

    private float textSize = 0F;

    @Transient
    private int[] beginTime = new int[]{800, 850, 955, 1045, 1135, 1400, 1450, 1550, 1640, 1825, 1915, 2005};

    public SyllabusSetting(String account) {
        this.account = account;
    }

    @Generated(hash = 1096443212)
    public SyllabusSetting(String account, int weekCnt, int nodeCnt, boolean showSaturday, boolean showSunday,
            String openingDay, int nodeLength, int firstDayOfWeek, String background, float textSize) {
        this.account = account;
        this.weekCnt = weekCnt;
        this.nodeCnt = nodeCnt;
        this.showSaturday = showSaturday;
        this.showSunday = showSunday;
        this.openingDay = openingDay;
        this.nodeLength = nodeLength;
        this.firstDayOfWeek = firstDayOfWeek;
        this.background = background;
        this.textSize = textSize;
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

    public float getTextSize() {
        return this.textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
}
