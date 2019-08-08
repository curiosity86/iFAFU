package cn.ifafu.ifafu.mvp.main

import android.content.Intent

import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.data.Response
import cn.ifafu.ifafu.mvp.base.BasePresenter
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter2
import cn.ifafu.ifafu.mvp.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

internal class MainPresenter(view: MainContract.View) : BaseZFPresenter2<MainContract.View>(view), MainContract.Presenter {

    private val mModel: MainModel = MainModel(view.context)

    override fun onStart() {
        mView?.setLeftMenuHeadName(mModel.getUserName())
        mModel.getSchoolIcon()?.let { mView?.setLeftMenuHeadIcon(it) }
        update()
    }

    override fun update() {
        // 登录
        addDisposable {
            mModel.login(mModel.user)
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ r ->
                        if (r.code == Response.ERROR) {
                            mView?.showMessage(r.message)
                        }
                    }, this::onError)
        }
        // 获取主页菜单
        addDisposable {
            mModel.getMenus()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { mView?.showLoading() }
                    .subscribe({ menus -> mView?.setMenuAdapterData(menus) }, this::onError)
        }
        // 获取天气
        addDisposable {
            mModel.getWeather("101230101")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ weather -> mView?.setWeatherText(weather) }, this::onError)
        }
    }

    override fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_SUBJECT, "分享")
            putExtra(Intent.EXTRA_TEXT, "iFAFU下载链接：http://ifafu.cn")
        }
        mView?.openActivity(Intent.createChooser(intent, "分享"))
    }

    override fun quitAccount() {
        if (BuildConfig.DEBUG) {
            mModel.clearAllDate()
        }
        val intent = Intent(mView?.context, LoginActivity::class.java)
        mView?.openActivity(intent)
        mView?.killSelf()
    }
}
