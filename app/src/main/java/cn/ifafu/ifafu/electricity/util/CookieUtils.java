package cn.ifafu.ifafu.electricity.util;

public class CookieUtils {
    public static String getCookie() {
        return getACookie("ASP.NET_SessionId") +
                getACookie("imeiticket") +
                getACookie("hallticket") +
                getACookie("username") +
                getACookie("sourcetypeticket");
    }

    private static String getACookie(String name) {
        String value = SPUtils.get("Cookie").getString(name);
        if (!value.isEmpty()) {
           return name + "=" + value + "; ";
        } else {
            return "";
        }
    }

}
