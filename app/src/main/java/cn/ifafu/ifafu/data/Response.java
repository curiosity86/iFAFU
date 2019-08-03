package cn.ifafu.ifafu.data;

import cn.ifafu.ifafu.data.announce.HttpCode;

public class Response<T> {

    public static final int SUCCESS = 200;
    public static final int FAILURE = 400;
    public static final int ERROR = 500;

    private int code;

    private String message;

    private T body;

    public Response() {
        message = "";
    }

    public Response(int code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(@HttpCode int code) {
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

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return this.code == SUCCESS;
    }

    public static <T> Response<T> success(T body) {
        return new Response<>(SUCCESS, "", body);
    }

    public static <T> Response<T> success(String msg, T body) {
        return new Response<>(SUCCESS, msg, body);
    }

    public static <T> Response<T> failure(String msg) {
        return new Response<>(FAILURE, msg, null);
    }

    public static <T> Response<T> failure(String msg, T body) {
        return new Response<>(FAILURE, msg, body);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(ERROR, msg, null);
    }
}
