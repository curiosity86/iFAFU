package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Random;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.announce.SchoolCode;

@Entity
public class User implements Serializable {

    @Id
    private String account; // 学号
    private String password; // 密码
    private String name; // 名字
    private String token; // Token

    @SchoolCode
    private int schoolCode = School.FAFU;

    private static final long serialVersionUID = 0x00010123;

    @Generated(hash = 1559455514)
    public User(String account, String password, String name, String token, int schoolCode) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.token = token;
        this.schoolCode = schoolCode;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getSchoolCode() {
        return this.schoolCode;
    }

    public void setSchoolCode(int schoolCode) {
        this.schoolCode = schoolCode;
    }

    public boolean isNull() {
        return account == null;
    }

}
