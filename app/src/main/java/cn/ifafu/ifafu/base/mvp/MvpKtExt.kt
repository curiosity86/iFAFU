package cn.ifafu.ifafu.base.mvp

import io.reactivex.disposables.Disposable

/**
 * Kotlin Mvp 扩展
 * Created by woolsen on 19/9/28
 */
fun BasePresenter<*, *>.addDisposable(init: () -> Disposable) {
    this.mCompDisposable.add(init())
}