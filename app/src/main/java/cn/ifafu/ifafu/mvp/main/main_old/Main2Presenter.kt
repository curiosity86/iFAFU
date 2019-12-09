package cn.ifafu.ifafu.mvp.main.main_old

import android.content.Intent
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.BaseMainPresenter
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable

class Main2Presenter(view: Main2Contract.View)
    : BaseMainPresenter<Main2Contract.View, Main2Contract.Model>(view, Main2Model(view.context)),
        Main2Contract.Presenter {

    override fun onCreate() {
        super.onCreate()
        addDisposable {
            Observable
                    .fromCallable {
                        mModel.getLoginUser() ?: throw NullPointerException()
                    }
                    .compose(RxUtils.computationToMain())
                    .subscribe({
                        mView.setAccountText(it.account)
                        mView.setNameText(it.name)
                    }, {
                        onError(it)
                        if (it is NullPointerException) {
                            mView.openActivity(Intent(mView.context, LoginActivity::class.java))
                            mView.killSelf()
                        }
                    })
        }
        addDisposable {
            Observable
                    .fromCallable { mModel.getFunctionTab() }
                    .compose(RxUtils.computationToMain())
                    .subscribe({
                        mView.makeLeftMenu(it)
                    }, this::onError)
        }
        updateWeather()
        mModel.getYearTerm().run {
            mView.setYearTermTitle("${first}学年第${second}学期")
        }
    }

    override fun updateWeather() {
        addDisposable {
            mModel.getWeather("101230101")
                    .map { "${it.nowTemp}℃ | ${it.weather}" }
                    .compose(RxUtils.ioToMain())
                    .subscribe({ mView.setWeatherText(it) }, this::onError)
        }
    }

    override fun updateNextCourse() {
        addDisposable {
            mModel.getNextCourse()
                    .compose(RxUtils.ioToMain())
                    .subscribe({
                        mView.setNextCourse(it)
                    }, this::onError)
        }
    }

    override fun updateExamInfo() {
        addDisposable {
            mModel.getNextExams()
                    .compose(RxUtils.ioToMain())
                    .subscribe({
                        mView.setExamData(it)
                    }, this::onError)
        }
    }

    override fun updateScoreInfo() {
        addDisposable {
            mModel.getScore()
                    .compose(RxUtils.ioToMain())
                    .subscribe({
                        if (it.isEmpty()) {
                            mView.setScoreText(null)
                        } else {
                            mView.setScoreText("已出${it.size}门成绩")
                        }
                    }, {
                        onError(it)
                        mView.setScoreText("获取成绩信息出错")
                    })
        }
    }

}