package cn.ifafu.ifafu.electricity.util;

import cn.ifafu.ifafu.app.Constant;

public class CookieUtils {
    public static String getCookie() {
        return getACookie("ASP.NET_SessionId") +
                getACookie("imeiticket") +
                getACookie("hallticket") +
                getACookie("username") +
                getACookie("sourcetypeticket");
    }

    private static String getACookie(String name) {
        String value = SPUtils.get(Constant.SP_COOKIE + "_Elec").getString(name);
        if (!value.isEmpty()) {
           return name + "=" + value + "; ";
        } else {
            return "";
        }
    }

}
