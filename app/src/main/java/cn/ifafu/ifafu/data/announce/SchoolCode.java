package cn.ifafu.ifafu.data.announce;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.ifafu.ifafu.app.Constant;

@IntDef(value = {Constant.FAFU, Constant.FAFU_JS})
@Retention(RetentionPolicy.SOURCE)
public @interface SchoolCode{

}