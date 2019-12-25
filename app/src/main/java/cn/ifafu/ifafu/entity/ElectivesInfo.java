package cn.ifafu.ifafu.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ElectivesInfo {

    @PrimaryKey
    private String account;

    private String institute;

    private String major;

    private int total; //任意选修课毕业学分要求

    private int zrkx; //自然科学类

    private int rwsk; //人文社科类

    private int ysty; //艺术体育类

    private int wxsy; //文学素养类

    private int cxcy; //创新创业教育类

    public ElectivesInfo(String account, String institute, String major, int total,
            int zrkx, int rwsk, int ysty, int wxsy, int cxcy) {
        this.account = account;
        this.institute = institute;
        this.major = major;
        this.total = total;
        this.zrkx = zrkx;
        this.rwsk = rwsk;
        this.ysty = ysty;
        this.wxsy = wxsy;
        this.cxcy = cxcy;
    }

    public ElectivesInfo() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getInstitute() {
        return this.institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getMajor() {
        return this.major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getZrkx() {
        return this.zrkx;
    }

    public void setZrkx(int zrkx) {
        this.zrkx = zrkx;
    }

    public int getRwsk() {
        return this.rwsk;
    }

    public void setRwsk(int rwsk) {
        this.rwsk = rwsk;
    }

    public int getYsty() {
        return this.ysty;
    }

    public void setYsty(int ysty) {
        this.ysty = ysty;
    }

    public int getWxsy() {
        return this.wxsy;
    }

    public void setWxsy(int wxsy) {
        this.wxsy = wxsy;
    }

    public int getCxcy() {
        return this.cxcy;
    }

    public void setCxcy(int cxcy) {
        this.cxcy = cxcy;
    }

    public void set(int zrkx, int rwsk, int ysty, int wxsy, int cxcy) {
        this.zrkx = zrkx;
        this.rwsk = rwsk;
        this.ysty = ysty;
        this.wxsy = wxsy;
        this.cxcy = cxcy;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
