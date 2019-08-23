package cn.ifafu.ifafu.view.timeline;

public class TimeAxis {
    private String name;
    private String date;
    private int day;

    public TimeAxis() {
    }

    public TimeAxis(String name, String date, int day) {
        this.name = name;
        this.date = date;
        this.day = day;
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
}
