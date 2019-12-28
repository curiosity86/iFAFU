package cn.ifafu.ifafu.entity.exception

class NoLogException : Exception {
    constructor() {}
    constructor(message: String?) : super(message) {}
}