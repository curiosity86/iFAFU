package cn.ifafu.ifafu.mvp.main

import android.content.Intent
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.util.RxUtils
import com.tencent.bugly.beta.Beta
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseMainPresenter<V : BaseMainContract.View, M : BaseMainContract.Model>(view: V, model: M)
    : BasePresenter<V, M>(view, model), BaseMainContract.Presenter {

    private var nowTheme: Int = -1

    override fun onCreate() {
        addDisposable {
            Observable.fromCallable {
                nowTheme = mModel.getSetting().theme
                nowTheme
            }
                    .compose(RxUtils.ioToMain())
                    .subscribe({
                        if (nowTheme != it) {
                            mView.openActivity(Intent(mView.context, MainActivity::class.java))
                            mView.killSelf()
                        }
                    }, this::onError)
        }

    }

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
        GlobalScope.launch(Dispatchers.IO) {
            val user = mModel.getLoginUser()
            launch(Dispatchers.Main) {
                mView.showMessage("已切换到${user?.account}")
                onCreate()
            }
        }
    }

    final override fun checkout() {
        // 多账号管理数据
        mCompDisposable.add(Observable.fromCallable { mModel.getAllUser() }
                .compose(RxUtils.computationToMain())
                .subscribe({
                    mView.setCheckoutDialogData(it)
                    mView.showCheckoutDialog()
                }, this::onError)
        )
    }

    final override fun deleteUser(user: User) {
        GlobalScope.launch(Dispatchers.IO) {
            mModel.deleteAccount(user)
            mModel.getLoginUser().run {
                launch(Dispatchers.Main) {
                    if (this@run == null) {
                        mView.openActivity(Intent(mView.activity, LoginActivity::class.java))
                        mView.killSelf()
                    } else {
                        mView.showMessage("已切换到${account}")
                        mView.hideCheckoutDialog()
                        onCreate()
                    }
                }
            }
        }
    }

    final override fun checkoutTheme() {
        mView.hideCheckoutDialog()
        mView.hideLoading()
        mView.openActivity(Intent(mView.context, MainActivity::class.java))
        mView.killSelf()
    }

    final override fun checkoutTo(user: User) {
        GlobalScope.launch(Dispatchers.IO) {
            if (user.account != mModel.getLoginUser()?.account) {
                mModel.saveLoginUser(user)
                launch(Dispatchers.Main) {
                    val ld = mModel.reLogin()
                            .compose(RxUtils.ioToMain())
                            .doOnSubscribe { mView.showLoading() }
                            .doFinally { mView.hideLoading() }
                            .subscribe({
                                mView.showMessage("成功切换到${user.account}")
                            }, this@BaseMainPresenter::onError)
                    IFAFU.loginDisposable = ld
                    mCompDisposable.add(ld)
                    mView.hideCheckoutDialog()
                    this@BaseMainPresenter.onCreate()
                }
            }
        }
    }
}
