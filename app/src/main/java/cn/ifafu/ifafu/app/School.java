package cn.ifafu.ifafu.app;

import java.util.HashMap;
import java.util.Map;

import cn.ifafu.ifafu.entity.User;
import cn.ifafu.ifafu.entity.ZFApi;
import cn.ifafu.ifafu.entity.ZhengFang;

public class School {

    public static final String FAFU = "FAFU";
    public static final String FAFU_JS = "FAFU_JS";

    private static final Map<String, ZhengFang> URL_MAP = new HashMap<>();

    static {
        URL_MAP.put(FAFU, new ZhengFang(FAFU, "http://jwgl.fafu.edu.cn/{token}/",
                "default2.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                new HashMap<String, ZFApi>() {{
                    put(ZhengFang.SYLLABUS, new ZFApi("xskbcx.aspx", "N121602"));
                    put(ZhengFang.EXAM, new ZFApi("xskscx.aspx", "N121604"));
                    put(ZhengFang.SCORE, new ZFApi("xscjcx_dq_fafu.aspx", "N121605"));
                    put(ZhengFang.COMMENT, new ZFApi("xsjxpj2fafu2.aspx", "N121400"));
                }}));
        URL_MAP.put(FAFU_JS, new ZhengFang(FAFU_JS, "http://js.ifafu.cn/",
                "default.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                new HashMap<String, ZFApi>() {{
                    put(ZhengFang.SYLLABUS, new ZFApi("xskbcx.aspx", "N121602"));
                    put(ZhengFang.EXAM, new ZFApi("xskscx.aspx", "N121603"));
                    put(ZhengFang.SCORE, new ZFApi("Xscjcx.aspx", "N121613"));
                }}));
    }

    /**
     * 获取对应Url
     *
     * @param filed {@link ZhengFang}
     * @param user  user
     * @return url
     */
    public static String getUrl(String filed, User user) {
        return URL_MAP.get(user.getSchoolCode()).get(filed, user);
    }

}
