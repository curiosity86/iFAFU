package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Token {

    @Id
    private String account;
    private String token;

    @Generated(hash = 1419332755)
    public Token(String account, String token) {
        this.account = account;
        this.token = token;
    }

    @Generated(hash = 79808889)
    public Token() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
