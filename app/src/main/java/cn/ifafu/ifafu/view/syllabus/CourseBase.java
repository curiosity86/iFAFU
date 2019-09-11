package cn.ifafu.ifafu.view.syllabus;

import androidx.annotation.NonNull;

import cn.ifafu.ifafu.util.ColorUtils;

public class CourseBase implements ToCourseBase {

    // 显示的文本
    private String text;

    // 星期
    private int weekday;

    // 显示开始的行数
    private int beginNode;

    // 显示的行数
    private int nodeCnt;

    // 显示的颜色
    private int color;

    public Object other;

    public CourseBase() {
        color = -1;
    }

    public CourseBase(String text, @DayOfWeek int weekday, int beginNode, int nodeNum) {
        this.text = text;
        this.weekday = weekday;
        this.beginNode = beginNode;
        this.nodeCnt = nodeNum;
        this.color = ColorUtils.getRandomColor();
    }

    public CourseBase(String text, @DayOfWeek int weekday, int beginNode, int nodeNum, int color) {
        this.text = text;
        this.weekday = weekday;
        this.beginNode = beginNode;
        this.nodeCnt = nodeNum;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(@DayOfWeek int weekday) {
        this.weekday = weekday;
    }

    public int getBeginNode() {
        return beginNode;
    }

    public void setBeginNode(int beginNode) {
        this.beginNode = beginNode;
    }

    public int getNodeCnt() {
        return nodeCnt;
    }

    public void setNodeCnt(int nodeCnt) {
        this.nodeCnt = nodeCnt;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }

    @Override
    public CourseBase toCourseBase() {
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "CourseBase{" +
                "text='" + text + '\'' +
                ", weekday=" + weekday +
                ", beginNode=" + beginNode +
                ", nodeCnt=" + nodeCnt +
                ", color=" + color +
                '}';
    }
}
