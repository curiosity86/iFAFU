package cn.ifafu.ifafu.data.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Score extends YearTerm {
    @Id
    private Long id;
    private String name; //课程名称
    private String nature; //课程性质
    private String attr; //课程归属
    private float credit = -1; //学分
    private float score = -1; //成绩
    private float makeupScore = -1; //补考成绩
    private boolean restudy = false; //是否重修
    private String institute; //开课学院
    private float gpa = -1; //绩点
    private String remarks; //备注
    private String makeupRemarks; //补考备注
    private boolean isIESItem = true; //是否记入智育分

    private String account;
    private String year;
    private String term;

    @Generated(hash = 984428122)
    public Score(Long id, String name, String nature, String attr, float credit,
            float score, float makeupScore, boolean restudy, String institute,
            float gpa, String remarks, String makeupRemarks, boolean isIESItem,
            String account, String year, String term) {
        this.id = id;
        this.name = name;
        this.nature = nature;
        this.attr = attr;
        this.credit = credit;
        this.score = score;
        this.makeupScore = makeupScore;
        this.restudy = restudy;
        this.institute = institute;
        this.gpa = gpa;
        this.remarks = remarks;
        this.makeupRemarks = makeupRemarks;
        this.isIESItem = isIESItem;
        this.account = account;
        this.year = year;
        this.term = term;
    }

    @Generated(hash = 226049941)
    public Score() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNature() {
        return this.nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public float getCredit() {
        return this.credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getMakeupScore() {
        return this.makeupScore;
    }

    public void setMakeupScore(float makeupScore) {
        this.makeupScore = makeupScore;
    }

    public boolean getRestudy() {
        return this.restudy;
    }

    public void setRestudy(boolean restudy) {
        this.restudy = restudy;
    }

    public String getInstitute() {
        return this.institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public float getGpa() {
        return this.gpa;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAttr() {
        return this.attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMakeupRemarks() {
        return this.makeupRemarks;
    }

    public void setMakeupRemarks(String makeupRemarks) {
        this.makeupRemarks = makeupRemarks;
    }

    public boolean getIsIESItem() {
        return this.isIESItem;
    }

    public void setIsIESItem(boolean isIESItem) {
        this.isIESItem = isIESItem;
    }

    public float getCalcScore() {
        if (score < 60) { //不及格
            if (makeupScore == -1) { //补考成绩未出，以原成绩计算
                return score;
            } else if (makeupScore >= 60) { //补考成绩及格，以60分计算
                return 60;
            } else { //补考成绩不及格，以补考成绩计算，并以学分1:1比例扣除相应智育分
                return makeupScore;
            }
        } else { //及格
            return score;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nature='" + nature + '\'' +
                ", credit=" + credit +
                ", score=" + score +
                ", makeupScore=" + makeupScore +
                ", restudy=" + restudy +
                ", institute='" + institute + '\'' +
                ", gpa=" + gpa +
                ", account='" + account + '\'' +
                ", year='" + year + '\'' +
                ", term='" + term + '\'' +
                '}';
    }
}
