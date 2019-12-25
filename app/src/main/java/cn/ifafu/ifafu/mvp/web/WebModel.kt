package cn.ifafu.ifafu.mvp.web

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.entity.ZhengFang
import io.reactivex.Observable

class WebModel internal constructor(context: Context) : BaseZFModel(context), WebContract.Model {

    override fun getMainUrl(): String {
        return School.getUrl(ZhengFang.MAIN, repository.getInUseUser())
    }

    override fun loadMainHtml(): Observable<MutableMap<String, String>> {
        return Observable.fromCallable { getMainUrl() }
                .flatMap { initParams(it, "") }
    }
}
