package cn.ifafu.ifafu.mvp.web

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import io.reactivex.Observable

class WebModel internal constructor(context: Context) : BaseZFModel(context), WebContract.Model {

    override fun getMainUrl(): String {
        return School.getUrl(ZhengFang.MAIN, repository.loginUser)
    }

    override fun loadMainHtml(): Observable<MutableMap<String, String>> {
        return initParams(getMainUrl(), "")
    }
}
