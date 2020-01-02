package cn.ifafu.ifafu.mvp.main.old

import android.content.Intent
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.BaseMainPresenter
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainOldPresenter(view: MainOldContract.View)
    : BaseMainPresenter<MainOldContract.View, MainOldContract.Model>(view, MainOldModel(view.context)),
        MainOldContract.Presenter {

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
        GlobalScope.launch {
            try {
                val scores = mModel.getScore()
                val message = if (scores.isEmpty()) {
                    "暂无成绩信息"
                } else {
                    "已出${scores.size}门成绩"
                }
                withContext(Dispatchers.Main) {
                    mView.setScoreText(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    mView.setScoreText("获取成绩信息出错")
                }
            }
        }
    }

}