package cn.ifafu.ifafu.data.announce;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.ifafu.ifafu.data.Response;

@IntDef(value = {Response.SUCCESS, Response.FAILURE, Response.ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface HttpCode {
}