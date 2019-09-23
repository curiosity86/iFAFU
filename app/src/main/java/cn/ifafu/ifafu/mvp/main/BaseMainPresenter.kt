package cn.ifafu.ifafu.mvp.main

import android.content.Intent
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.util.RxUtils
import com.tencent.bugly.beta.Beta
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseMainPresenter<V : BaseMainContract.View, M : BaseMainContract.Model>(view: V, model: M)
    : BasePresenter<V, M>(view, model), BaseMainContract.Presenter {

    final override fun updateApp() {
        mCompDisposable.add(Observable
                .fromCallable {
                    val info = Beta.getUpgradeInfo()
                    info != null && info.versionCode > GlobalLib.getLocalVersionCode(mView.activity)
                }
                .compose(RxUtils.ioToMain())
                .subscribe({ aBoolean ->
                    if (aBoolean!!) {
                        Beta.checkUpgrade()
                    } else {
                        mView.showMessage(R.string.is_last_version)
                    }
                }, this::onError)
        )
    }

    protected fun addDisposable(disposable: () -> Disposable) {
        mCompDisposable.add(disposable.invoke())
    }

    override fun addAccountSuccess() {
        mView.showMessage("已切换到${mModel.getLoginUser()?.account}")
        onCreate()
    }

    final override fun checkout() {
        // 多账号管理数据
        mCompDisposable.add(Observable
                .just(mModel.getAllUser())
                .compose(RxUtils.computationToMain())
                .subscribe({
                    mView.setCheckoutDialogData(it)
                    mView.showCheckoutDialog()
                }, this::onError)
        )
    }

    final override fun deleteUser(user: User) {
        mModel.deleteAccount(user)
        mModel.getLoginUser().run {
            if (this == null) {
                mView.openActivity(Intent(mView.activity, LoginActivity::class.java))
                mView.killSelf()
            } else {
                mView.showMessage("已切换到${account}")
                mView.hideCheckoutDialog()
                onCreate()
            }
        }
    }

    final override fun checkoutTheme() {
        mView.openActivity(Intent(mView.context, MainActivity::class.java))
        mView.killSelf()
    }

    final override fun checkoutTo(user: User) {
        if (user.account == mModel.getLoginUser()?.account) {
            return
        } else {
            mModel.saveLoginUser(user)
            val ld = mModel.reLogin()
                    .compose(RxUtils.ioToMain())
                    .doOnSubscribe { mView.showLoading() }
                    .doFinally { mView.hideLoading() }
                    .subscribe({
                        mView.showMessage("成功切换到${user.account}")
                    }, this::onError)
            IFAFU.loginDisposable = ld
            mCompDisposable.add(ld)
            mView.hideCheckoutDialog()
            this.onCreate()
        }
    }
}
