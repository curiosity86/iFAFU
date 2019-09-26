package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

@Entity
public class Setting {

    @Id
    private String account;

    private int theme = THEME_NEW;

    public static final int THEME_NEW = 0;
    public static final int THEME_OLD = 1;

    @Generated(hash = 1461917542)
    public Setting(String account, int theme) {
        this.account = account;
        this.theme = theme;
    }

    public Setting(String account) {
        this.account = account;
    }

    @Generated(hash = 909716735)
    public Setting() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getTheme() {
        return this.theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return theme == setting.theme &&
                Objects.equals(account, setting.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, theme);
    }
}
