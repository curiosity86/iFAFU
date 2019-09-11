package cn.ifafu.ifafu.data.entity;

import java.util.HashMap;
import java.util.Map;

public class Holiday {

    private String name;

    private String date; //开始放假时间

    private int day; //放假天数

    private Map<String, String> fromTo; //调课方式， 从key调到value

    public Holiday(String name, String date, int day) {
        this.name = name;
        this.day = day;
        this.date = date;
    }
    public Holiday(String name, String date, int day, Map<String, String> fromTo) {
        this.name = name;
        this.date = date;
        this.day = day;
        this.fromTo = fromTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    /**
     * 添加调课日期
     * @param from 调课前日期
     * @param to 调课后日期
     */
    public void addFromTo(String from, String to) {
        if (fromTo == null) {
            fromTo = new HashMap<>();
        }
        fromTo.put(from, to);
    }

    public Map<String, String> getFromTo() {
        return fromTo;
    }
}
