package cn.ifafu.ifafu.mvp.base

import cn.ifafu.ifafu.mvp.base.i.IView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseZFPresenter2<V: IView>(protected var mView: V?): IZFPresenter {

    private val mCompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: () -> Disposable) {
        mCompositeDisposable.add(disposable())
    }

    override fun onDestroy() {
        mView = null
        mCompositeDisposable.dispose()
    }

    protected fun onError(throwable: Throwable) {
        mView?.showMessage(throwable.message)
        throwable.printStackTrace()
    }

}
