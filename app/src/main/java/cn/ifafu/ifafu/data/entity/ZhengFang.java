package cn.ifafu.ifafu.data.entity;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;

import cn.ifafu.ifafu.data.local.RepositoryImpl;

public class ZhengFang {

    private int schoolCode;

    public static final String LOGIN = "LOGIN";
    public static final String VERIFY = "VERIFY";
    public static final String MAIN = "MAIN";
    public static final String SYLLABUS = "SYLLABUS";
    public static final String EXAM = "EXAM";
    public static final String SCORE = "SCORE";
    public static final String COMMENT = "COMMENT";

    private String baseUrl;
    private String login;
    private String verify;
    private String main;
    private Map<String, ZFApi> apiMap;

    private String baseUrlTemp;
    private String accountTemp;

    public ZhengFang(int schoolCode, String baseUrl, String login, String verify, String main, Map<String, ZFApi> apiMap) {
        this.schoolCode = schoolCode;
        this.baseUrl = baseUrl;
        this.login = login;
        this.verify = verify;
        this.main = main;
        this.apiMap = apiMap;
    }

    private String getBaseUrl(String account) {
        if (baseUrlTemp == null || !account.equals(accountTemp)) {
            baseUrlTemp = baseUrl
                    .replace("{token}", getToken(account));
            accountTemp = account;
        }
        return baseUrlTemp;
    }

    private String getToken(String account) {
        Token t = RepositoryImpl.getInstance().getToken(account);
        if (t == null)  {
            char[] randomStr = "abcdefghijklmnopqrstuvwxyz12345".toCharArray();
            StringBuilder token = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 24; i++) {
                token.append(randomStr[random.nextInt(31)]);
            }
            t = new Token(account, token.toString());
            RepositoryImpl.getInstance().saveToken(t);
        }
        return '(' + t.getToken() + ')';
    }

    public String get(String filed, String xh, String xm) {
        switch (filed) {
            case VERIFY:
                return String.format("%s%s", getBaseUrl(xh), verify);
            case LOGIN:
                return String.format("%s%s", getBaseUrl(xh), login);
            case MAIN:
                return String.format("%s%s?xh=%s", getBaseUrl(xh), main, xh);
            default:
                try {
                    ZFApi api = apiMap.get(filed);
                    if (api != null) {
                        return String.format("%s%s?xh=%s&xm=%s&gnmkdm=%s",
                                getBaseUrl(xh), api.getApi(), xh, URLEncoder.encode(xm, "GBK"), api.getGnmkdm());
                    } else {
                        throw new IllegalArgumentException("url is not found");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "";
                }
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "ZhengFang{" +
                "schoolCode=" + schoolCode +
                ", baseUrl='" + baseUrl + '\'' +
                ", login='" + login + '\'' +
                ", verify='" + verify + '\'' +
                ", main='" + main + '\'' +
                ", baseUrlTemp='" + baseUrlTemp + '\'' +
                ", apiMap=" + JSONObject.toJSONString(apiMap) +
                '}';
    }
}
