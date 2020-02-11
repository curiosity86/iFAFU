package cn.ifafu.ifafu.data.entity.exception

class NoLogException : Exception {
    constructor() {}
    constructor(message: String?) : super(message) {}
}