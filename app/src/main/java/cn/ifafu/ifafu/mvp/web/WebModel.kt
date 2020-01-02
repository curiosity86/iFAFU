package cn.ifafu.ifafu.mvp.web

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvp.BaseModel
import cn.ifafu.ifafu.entity.ZFApiList
import io.reactivex.Observable

class WebModel internal constructor(context: Context) : BaseModel(context), WebContract.Model {

    override fun getMainUrl(): String {
        return School.getUrl(ZFApiList.MAIN, mRepository.getInUseUser()!!)
    }

    override fun loadMainHtml(): Observable<MutableMap<String, String>> {
        return Observable.fromCallable { getMainUrl() }
                .flatMap { initParams(it, "") }
    }
}
