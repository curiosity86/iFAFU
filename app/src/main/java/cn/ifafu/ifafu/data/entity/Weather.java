package cn.ifafu.ifafu.data.entity;

import androidx.annotation.NonNull;

public class Weather {

    private String cityName;
    private int nowTemp;
    private int amTemp;
    private int pmTemp;
    private String weather;

    public Weather() {
    }

    public Weather(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getNowTemp() {
        return nowTemp;
    }

    public void setNowTemp(int nowTemp) {
        this.nowTemp = nowTemp;
    }

    public int getAmTemp() {
        return amTemp;
    }

    public void setAmTemp(int amTemp) {
        this.amTemp = amTemp;
    }

    public int getPmTemp() {
        return pmTemp;
    }

    public void setPmTemp(int pmTemp) {
        this.pmTemp = pmTemp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @NonNull
    @Override
    public String toString() {
        return "Weather{" +
                "cityName='" + cityName + '\'' +
                ", nowTemp=" + nowTemp +
                ", amTemp=" + amTemp +
                ", pmTemp=" + pmTemp +
                ", weather='" + weather + '\'' +
                '}';
    }
}
