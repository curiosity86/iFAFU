package cn.ifafu.ifafu.data.announce;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.ifafu.ifafu.app.School;

@IntDef(value = {School.FAFU, School.FAFU_JS})
@Retention(RetentionPolicy.SOURCE)
public @interface SchoolCode {

}