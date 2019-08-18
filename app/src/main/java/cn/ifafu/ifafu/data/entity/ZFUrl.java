package cn.ifafu.ifafu.data.entity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;

public class ZFUrl {

    public static final int LOGIN = 843;
    public static final int VERIFY = 920;
    public static final int MAIN = 99;
    public static final int SYLLABUS = 354;
    public static final int EXAM = 985;
    public static final int SCORE = 284;

    private int schoolCode;
    private String baseUrl;
    private String login;
    private String verify;
    private String main;

    private String baseUrlTemp;

    private Map<Integer, QueryApi> queryApiMap;

    public ZFUrl(int schoolCode, String baseUrl, String login, String verify, String main, Map<Integer, QueryApi> queryApiMap) {
        this.schoolCode = schoolCode;
        this.baseUrl = baseUrl;
        this.login = login;
        this.verify = verify;
        this.main = main;
        this.queryApiMap = queryApiMap;
    }

    private String getBaseUrl() {
        if (baseUrlTemp == null) {
            baseUrlTemp = baseUrl
                    .replace("{token}", makeToken());
        }
        return baseUrlTemp;
    }

    private String makeToken() {
        char[] randomStr = "abcdefghijklmnopqrstuvwxyz12345".toCharArray();
        StringBuilder token = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            token.append(randomStr[random.nextInt(31)]);
        }
        return '(' + token.toString() + ')';
    }

    public String get2(int filed, String xh, String xm) {
        switch (filed) {
            case VERIFY:
                return String.format("%s%s", getBaseUrl(), verify);
            case LOGIN:
                return String.format("%s%s", getBaseUrl(), login);
            case MAIN:
                return String.format("%s%s?xh=%s", getBaseUrl(), main, xh);
            default:
                try {
                    QueryApi api = queryApiMap.get(filed);
                    if (api != null) {
                        return String.format("%s%s?xh=%s&xm=%s&gnmkdm=%s",
                                getBaseUrl(), api.getApi(), xh, URLEncoder.encode(xm, "GBK"), api.getGnmkdm());
                    } else {
                        throw new IllegalArgumentException("url is not found");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "";
                }
        }
    }
}
