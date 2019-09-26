package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.HashMap;
import java.util.Map;

import cn.ifafu.ifafu.data.local.converter.StringMapConverter;

@Entity
public class ElecCookie {

    @Id
    private String account;

    private String rescouseType;

    @Convert(converter = StringMapConverter.class, columnType = String.class)
    private Map<String, Object> map = new HashMap<>();

    @Generated(hash = 1999390214)
    public ElecCookie(String account, String rescouseType,
            Map<String, Object> map) {
        this.account = account;
        this.rescouseType = rescouseType;
        this.map = map;
    }

    @Generated(hash = 1777254015)
    public ElecCookie() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public void set(String name, String value) {
        map.put(name, value);
    }

    public String get(String name) {
        Object ret = map.get(name);
        return ret != null ? ret.toString() : "";
    }

    public String toCookieString() {
        return getACookie("ASP.NET_SessionId") +
                getACookie("imeiticket") +
                getACookie("hallticket") +
                getACookie("username") +
                getACookie("sourcetypeticket");
    }

    private String getACookie(String name) {
        Object value = map.get(name);
        if (value != null) {
            return name + "=" + value.toString() + "; ";
        } else {
            return "";
        }
    }

    public String getRescouseType() {
        return this.rescouseType;
    }

    public void setRescouseType(String rescouseType) {
        this.rescouseType = rescouseType;
    }

}
