package cn.ifafu.ifafu.data.entity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public class ZFUrl {

    public static final int LOGIN = 843;
    public static final int VERIFY = 920;
    public static final int MAIN = 99;
    public static final int SYLLABUS = 354;
    public static final int EXAM = 985;

    private int schoolCode;
    private String baseUrl;
    private String login;
    private String verify;
    private String main;
    private QueryApi syllabus;
    private QueryApi exam;
    private String baseUrlTemp;

    public ZFUrl(int schoolCode, String baseUrl, String login, String verify, String main, QueryApi syllabus, QueryApi exam) {
        this.schoolCode = schoolCode;
        this.baseUrl = baseUrl;
        this.login = login;
        this.verify = verify;
        this.main = main;
        this.syllabus = syllabus;
        this.exam = exam;
    }

    public String getBaseUrl() {
        if (baseUrlTemp != null) {
            return baseUrlTemp;
        }
        baseUrlTemp = baseUrl
                .replace("{token}", makeToken());
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

    public String get(int filed, String xh, String xm) {
        try {
            String baseUrl = getBaseUrl();
            switch (filed) {
                case VERIFY:
                    return String.format("%s%s", baseUrl, verify);
                case LOGIN:
                    return String.format("%s%s", baseUrl, login);
                case MAIN:
                    return String.format("%s%s?xh=%s", baseUrl, main, xh);
                case SYLLABUS:
                    return String.format("%s%s?xh=%s&xm=%s&gnmkdm=%s",
                            baseUrl, syllabus.getApi(), xh, URLEncoder.encode(xm, "GBK"), syllabus.getGnmkdm());
                case EXAM:
                    return String.format("%s%s?xh=%s&xm=%s&gnmkdm=%s",
                            baseUrl, exam.getApi(), xh, URLEncoder.encode(xm, "GBK"), exam.getGnmkdm());
                default:
                    throw new IllegalArgumentException("field is invalid");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
