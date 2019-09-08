package cn.ifafu.ifafu.data.exception;

/**
 * 登录信息错误
 * 账号错误 or 密码错误
 */
public class LoginInfoErrorException extends Exception {

    public LoginInfoErrorException() {
    }

    public LoginInfoErrorException(String message) {
        super(message);
    }
}
