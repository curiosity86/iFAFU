package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Notice {

    @Id
    private String url;

    private String title;

    private String date;

    private boolean read;

    @Generated(hash = 1502420253)
    public Notice(String url, String title, String date, boolean read) {
        this.url = url;
        this.title = title;
        this.date = date;
        this.read = read;
    }

    @Generated(hash = 1880392847)
    public Notice() {
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getRead() {
        return this.read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


}
