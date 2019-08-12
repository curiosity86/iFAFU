package cn.ifafu.ifafu;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.QueryApi;
import cn.ifafu.ifafu.data.entity.ZFUrl;

public class UnitTest {

    public static final int FAFU = 0x001;
    public static final int FAFU_JS = 0x002;

    public static final Map<Integer, ZFUrl> URL_MAP = new HashMap<>();

    static {
        URL_MAP.put(FAFU, new ZFUrl(FAFU, "http://jwgl.fafu.edu.cn/",
                "default2.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                new QueryApi("xskbcx.aspx", "N121602"),
                new QueryApi("xscjcx_dq_fafu.aspx", "N121605")));
        URL_MAP.put(FAFU_JS, new ZFUrl(FAFU_JS, "http://js.ifafu.cn/",
                "default.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                new QueryApi("xskbcx.aspx", "N121602"),
                new QueryApi("Xscjcx.aspx", "N121613")));
    }

    @Test
    public void test() {
        System.out.println(JSONObject.toJSONString(URL_MAP));
    }
}
