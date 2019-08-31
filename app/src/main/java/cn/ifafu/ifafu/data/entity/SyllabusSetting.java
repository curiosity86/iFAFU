package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SyllabusSetting {

    @Id
    private String account;

    private int weekCnt = 24; //总共周数

    private int nodeCnt = 12; //每日课程节数

    private boolean showSaturday = true; //显示周六

    private boolean showSunday = true; //显示周日

    @Generated(hash = 1170150)
    public SyllabusSetting(String account, int weekCnt, int nodeCnt,
            boolean showSaturday, boolean showSunday) {
        this.account = account;
        this.weekCnt = weekCnt;
        this.nodeCnt = nodeCnt;
        this.showSaturday = showSaturday;
        this.showSunday = showSunday;
    }

    @Generated(hash = 310423812)
    public SyllabusSetting() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getWeekCnt() {
        return this.weekCnt;
    }

    public void setWeekCnt(int weekCnt) {
        this.weekCnt = weekCnt;
    }

    public int getNodeCnt() {
        return this.nodeCnt;
    }

    public void setNodeCnt(int nodeCnt) {
        this.nodeCnt = nodeCnt;
    }

    public boolean getShowSaturday() {
        return this.showSaturday;
    }

    public void setShowSaturday(boolean showSaturday) {
        this.showSaturday = showSaturday;
    }

    public boolean getShowSunday() {
        return this.showSunday;
    }

    public void setShowSunday(boolean showSunday) {
        this.showSunday = showSunday;
    }

}
