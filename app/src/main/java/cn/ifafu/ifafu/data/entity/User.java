package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Random;

import cn.ifafu.ifafu.app.Constant.SchoolCode;

@Entity
public class User implements Serializable {

    @Id
    private String account; // 学号
    private String password; // 密码
    private String name; // 名字
    private String token; // Token

    @SchoolCode
    private int schoolCode;

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

    public User(@SchoolCode int schoolCode) {
        this.schoolCode = schoolCode;
    }

    private String makeToken() {
        String randomString = "abcdefghijklmnopqrstuvwxyz12345";
        StringBuilder token = new StringBuilder("(");
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 24; i++) {
            token.append(randomString.charAt(random.nextInt(randomString.length())));
        }
        token.append(")/");
        return token.toString();
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
        if (token == null || token.isEmpty()) {
            this.token = makeToken();
        }
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getSchoolCode() {
        return this.schoolCode;
    }

    public void setSchoolCode(@SchoolCode int schoolCode) {
        this.schoolCode = schoolCode;
    }

    public boolean isNull() {
        return account == null;
    }

}
