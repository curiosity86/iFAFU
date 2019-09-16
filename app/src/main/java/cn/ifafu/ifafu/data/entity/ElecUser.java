package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ElecUser {

    @Id
    private String account;

    private String xfbAccount;

    private String password;

    private String name;

    @Generated(hash = 779410927)
    public ElecUser(String account, String xfbAccount, String password,
            String name) {
        this.account = account;
        this.xfbAccount = xfbAccount;
        this.password = password;
        this.name = name;
    }

    @Generated(hash = 1170065613)
    public ElecUser() {
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

    public String getXfbAccount() {
        return this.xfbAccount;
    }

    public void setXfbAccount(String xfbAccount) {
        this.xfbAccount = xfbAccount;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
