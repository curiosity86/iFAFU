package cn.ifafu.ifafu.view.syllabus.listener;

import android.view.View;

import cn.ifafu.ifafu.view.syllabus.CourseBase;

public interface OnCourseLongClickListener {
    boolean onLongClick(View v, CourseBase course);
}