package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ZFUrl {
    
    @Id
    private String account;

    private String referer;

    private String syllabus;

    private String exam;

    @Generated(hash = 1751367875)
    public ZFUrl(String account, String referer, String syllabus, String exam) {
        this.account = account;
        this.referer = referer;
        this.syllabus = syllabus;
        this.exam = exam;
    }

    @Generated(hash = 1511831326)
    public ZFUrl() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getReferer() {
        return this.referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getSyllabus() {
        return this.syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getExam() {
        return this.exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }



}
