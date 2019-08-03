package cn.ifafu.ifafu.data.announce;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.ifafu.ifafu.data.entity.Course;

@IntDef(value = {Course.ALL_WEEK, Course.SINGLE_WEEK, Course.DOUBLE_WEEK})
@Retention(RetentionPolicy.SOURCE)
public @interface WeekType {
}