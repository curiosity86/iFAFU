package cn.ifafu.ifafu.data.exception

class NoLogException : Exception {
    constructor() {}
    constructor(message: String?) : super(message) {}
}