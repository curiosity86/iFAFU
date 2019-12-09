package cn.ifafu.ifafu.data.entity;

import java.util.Map;

public class Response<T> {

    public static final int SUCCESS = 200;
    public static final int FAILURE = 400;
    public static final int ERROR = 500;

    private int code;

    private String message;

    private T body;

    private Map<String, String> hiddenParams;

    public Response() {
    }

    public Response(int code, T body, String message) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getHiddenParams() {
        return hiddenParams;
    }

    public void setHiddenParams(Map<String, String> hiddenParams) {
        this.hiddenParams = hiddenParams;
    }

    public boolean isSuccess() {
        return this.code == SUCCESS;
    }

    public static <T> Response<T> success(T body) {
        return new Response<>(SUCCESS, body , null);
    }

    public static <T> Response<T> success(T body, String msg) {
        return new Response<>(SUCCESS, body, msg);
    }

    public static <T> Response<T> failure(String msg) {
        return new Response<>(FAILURE, null, msg);
    }

    public static <T> Response<T> failure(T body, String msg) {
        return new Response<>(FAILURE, body, msg);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(ERROR, null, msg);
    }

}
