package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.announce.SchoolCode;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class User {

    @Id
    private String account; // 学号
    private String password; // 密码
    private String name; // 名字

    @SchoolCode
    private int schoolCode = School.FAFU;

    @Generated(hash = 201153438)
    public User(String account, String password, String name, int schoolCode) {
        this.account = account;
        this.password = password;
        this.name = name;
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

    public int getSchoolCode() {
        return this.schoolCode;
    }

    public void setSchoolCode(int schoolCode) {
        this.schoolCode = schoolCode;
    }

}
