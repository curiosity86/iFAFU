package cn.ifafu.ifafu.app;

import android.util.SparseArray;

import java.util.HashMap;

import cn.ifafu.ifafu.data.entity.QueryApi;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.entity.User;

public class School {

    public static final int FAFU = 1;
    public static final int FAFU_JS = 2;

    private static final SparseArray<ZFUrl> URL_MAP = new SparseArray<>();

    static {

        URL_MAP.put(FAFU, new ZFUrl(FAFU, "http://jwgl.fafu.edu.cn/{token}/",
                "default2.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                new HashMap<String, QueryApi>() {{
                    put(ZFUrl.SYLLABUS, new QueryApi("xskbcx.aspx", "N121602"));
                    put(ZFUrl.EXAM, new QueryApi("xskscx.aspx", "N121605"));
                    put(ZFUrl.SCORE, new QueryApi("xscjcx_dq_fafu.aspx", "N121605"));
                }}));
        URL_MAP.put(FAFU_JS, new ZFUrl(FAFU_JS, "http://js.ifafu.cn/",
                "default.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                new HashMap<String, QueryApi>() {{
                    put(ZFUrl.SYLLABUS, new QueryApi("xskbcx.aspx", "N121602"));
                    put(ZFUrl.EXAM, new QueryApi("Xscjcx.aspx", "N121613"));
                    put(ZFUrl.SCORE, new QueryApi("xscjcx_dq_fafu.aspx", "N121605"));
                }}));
    }

    /**
     * 获取对应Url
     *
     * @param filed {@link ZFUrl}
     * @param user  user
     * @return url
     */
    public static String getUrl(String filed, User user) {
        return URL_MAP.get(user.getSchoolCode()).get(filed, user.getAccount(), user.getName());
    }

}