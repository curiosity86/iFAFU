package cn.ifafu.ifafu.data.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.TreeSet;

import cn.ifafu.ifafu.data.local.IntTreeSetConverter;
import cn.ifafu.ifafu.view.syllabus.data.CourseBase;
import cn.ifafu.ifafu.view.syllabus.data.ToCourse;

@Entity
public class Course implements ToCourse {

    @Id
    private Long id;
    private String name; // 课程名
    private String address; // 上课地点
    private String teacher; // 老师名

    private int weekday; // 星期几
    private int beginNode; // 开始节数
    private int nodeCnt = 0; // 上课节数

    @Convert(converter = IntTreeSetConverter.class, columnType = String.class)
    private TreeSet<Integer> weekSet = new TreeSet<>();

    private int color; // 课程颜色
    private String account; // 课程归属账号
    private boolean local; // 是否是自定义课程

    @Generated(hash = 2140908675)
    public Course(Long id, String name, String address, String teacher, int weekday,
            int beginNode, int nodeCnt, TreeSet<Integer> weekSet, int color,
            String account, boolean local) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.teacher = teacher;
        this.weekday = weekday;
        this.beginNode = beginNode;
        this.nodeCnt = nodeCnt;
        this.weekSet = weekSet;
        this.color = color;
        this.account = account;
        this.local = local;
    }

    @Generated(hash = 1355838961)
    public Course() {
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

    public void setWeekday(int weekday) {
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

    public TreeSet<Integer> getWeekSet() {
        return this.weekSet;
    }

    public void setWeekSet(TreeSet<Integer> weekSet) {
        this.weekSet = weekSet;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
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

    @NonNull
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", teacher='" + teacher + '\'' +
                ", weekday=" + weekday +
                ", beginNode=" + beginNode +
                ", nodeCnt=" + nodeCnt +
                ", weekSet=" + weekSet +
                ", color=" + color +
                ", account='" + account + '\'' +
                ", local=" + local +
                '}';
    }
}
