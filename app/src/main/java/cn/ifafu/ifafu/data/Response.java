package cn.ifafu.ifafu.data;

import cn.ifafu.ifafu.data.announce.HttpCode;

public class Response<T> {

    public static final int SUCCESS = 200;
    public static final int FAILURE = 400;
    public static final int ERROR = 500;

    private int code;

    private String message;

    private T body;

    private String viewState;

    private String viewStateGenerator;

    private Response(int code, T body, String message) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    private Response(int code, T body, String message, String viewState, String viewStateGenerator) {
        this.code = code;
        this.message = message;
        this.body = body;
        this.viewState = viewState;
        this.viewStateGenerator = viewStateGenerator;
    }

    public int getCode() {
        return code;
    }

    public T getBody() {
        return body;
    }

    public String getMessage() {
        return message;
    }

    public String getViewState() {
        return viewState;
    }

    public String getViewStateGenerator() {
        return viewStateGenerator;
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

    public static <T> Response<T> success(String msg, T body, String viewState, String viewStateGenerator) {
        return new Response<T>(SUCCESS, body, msg, viewState, viewStateGenerator);
    }

    public static <T> Response<T> failure(String msg) {
        return new Response<>(FAILURE, null, msg);
    }

    public static <T> Response<T> failure(String msg, T body) {
        return new Response<>(FAILURE, body, msg);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(ERROR, null, msg);
    }

}
