package cn.ifafu.ifafu.app;


import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.SchoolApi;


public class Constant {
    public static final String DB_NAME  = "ifafu";

    public static final String IFAFU_BASE_URL  = "https://api.ifafu.cn";

    public static final String URL_FAFU = "http://jwgl.fafu.edu.cn/";
    public static final String URL_FAFU_JS = "http://js.ifafu.cn/";
    public static final String HOST_CARD = "http://cardapp.fafu.edu.cn:8088";
    public static final String HOST_BACKEND = "https://api.ifafu.cn";


    public static final String IFAFU_FILE_URL = "http://fafu.zxlee.cn/iFAFUFile/index.html";
    public static final String HELP_URL = "https://static.ifafu.cn/iFAFUHelp/index.html";

    public static final String SP_USER_INFO = "user_info";
    public static final String SP_SETTING = "setting";

    public static final int FAFU = 0x00010001;
    public static final int FAFU_JS = 0x00010002;

    @IntDef(value = {Constant.FAFU, Constant.FAFU_JS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SchoolCode{}

}
