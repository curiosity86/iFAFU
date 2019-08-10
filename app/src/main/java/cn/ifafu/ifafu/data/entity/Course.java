package cn.ifafu.ifafu.data.entity;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

import cn.ifafu.ifafu.data.announce.WeekType;
import cn.ifafu.ifafu.view.syllabus.data.CourseBase;
import cn.ifafu.ifafu.view.syllabus.data.DayOfWeek;
import cn.ifafu.ifafu.view.syllabus.data.ToCourse;

@Entity
public class Course extends Search implements Cloneable, ToCourse {
    @Id
    private Long id;
    private String name; // 课程名
    private String address; // 上课地点
    private String teacher; // 老师名

    private int weekday; // 星期几
    private int beginNode; // 开始节数
    private int nodeCnt = 0; // 上课节数

    private int beginWeek = 0; // 开始周
    private int endWeek; // 结束周
    private int weekType;  // 单双周

    private String timeString; // 教务管理系统上的显示的时间
    private int color; // 课程颜色
    private String account; // 课程归属账号
    private boolean local; // 是否是自定义课程

    public static final int ALL_WEEK = 8;
    public static final int SINGLE_WEEK = 9;
    public static final int DOUBLE_WEEK = 10;



    @Generated(hash = 50667648)
    public Course(Long id, String name, String address, String teacher, int weekday, int beginNode, int nodeCnt, int beginWeek,
            int endWeek, int weekType, String timeString, int color, String account, boolean local) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.teacher = teacher;
        this.weekday = weekday;
        this.beginNode = beginNode;
        this.nodeCnt = nodeCnt;
        this.beginWeek = beginWeek;
        this.endWeek = endWeek;
        this.weekType = weekType;
        this.timeString = timeString;
        this.color = color;
        this.account = account;
        this.local = local;
    }

    @Generated(hash = 1355838961)
    public Course() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, teacher, beginNode, nodeCnt, weekday, beginWeek, endWeek, weekType, account, local);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", teacher='" + teacher + '\'' +
                ", weekday=" + weekday +
                ", beginNode=" + beginNode +
                ", nodeCnt=" + nodeCnt +
                ", beginWeek=" + beginWeek +
                ", endWeek=" + endWeek +
                ", weekType=" + weekType +
                ", account='" + account + '\'' +
                ", local=" + local +
                '}';
    }

    @Override
    public CourseBase toCourseBase() {
        CourseBase courseBase = new CourseBase();
        courseBase.setText(name + "\n@" + address);
        courseBase.setBeginNode(beginNode);
        courseBase.setWeekday(weekday);
        courseBase.setNodeCnt(nodeCnt);
        courseBase.setOther(this);
        return courseBase;
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

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getWeekday() {
        return this.weekday;
    }

    public void setWeekday(@DayOfWeek int weekday) {
        this.weekday = weekday;
    }

    public int getBeginNode() {
        return this.beginNode;
    }

    public void setBeginNode(int beginNode) {
        this.beginNode = beginNode;
    }

    public int getNodeCnt() {
        return this.nodeCnt;
    }

    public void setNodeCnt(int nodeCnt) {
        this.nodeCnt = nodeCnt;
    }

    public int getBeginWeek() {
        return this.beginWeek;
    }

    public void setBeginWeek(int beginWeek) {
        this.beginWeek = beginWeek;
    }

    public int getEndWeek() {
        return this.endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getWeekType() {
        return this.weekType;
    }

    public void setWeekType(@WeekType int weekType) {
        this.weekType = weekType;
    }

    public String getTimeString() {
        return this.timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean getLocal() {
        return this.local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

}
