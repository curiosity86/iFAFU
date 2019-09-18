package cn.ifafu.ifafu.data.http.parser

import io.reactivex.Observable
import io.reactivex.ObservableSource
import okhttp3.ResponseBody

class LoginParamParser : ParamsParser() {

    override fun apply(upstream: Observable<ResponseBody>): ObservableSource<MutableMap<String, String>> {
        return upstream.map { responseBody -> parse(responseBody.string()) }
    }
}
