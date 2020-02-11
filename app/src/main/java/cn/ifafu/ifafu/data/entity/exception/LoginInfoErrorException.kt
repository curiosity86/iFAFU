package cn.ifafu.ifafu.data.entity.exception

/**
 * 登录信息错误
 * 账号错误 or 密码错误
 */
class LoginInfoErrorException : Exception {
    constructor() {}
    constructor(message: String?) : super(message) {}
}