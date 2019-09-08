package cn.ifafu.ifafu.mvp.score

import android.content.Intent
import cn.ifafu.ifafu.R.string
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter
import cn.ifafu.ifafu.mvp.score_filter.ScoreFilterActivity
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable

internal class ScorePresenter(view: ScoreContract.View)
    : BaseZFPresenter<ScoreContract.View, ScoreContract.Model>(view, ScoreModel(view.context)), ScoreContract.Presenter {

    private lateinit var years: List<String>
    private lateinit var terms: List<String>
    private var mCurrentYear: String? = null
    private var mCurrentTerm: String? = null

    override fun onCreate() {
        mCompDisposable.add(mModel
                .yearTermList
                .doOnNext { map: Map<String, List<String>> ->
                    years = map["ddlxn"] ?: error("")
                    terms = map["ddlxq"] ?: error("")
                }
                .flatMap { mModel.yearTerm }
                .doOnNext { map: Map<String, String> ->
                    mCurrentYear = map["ddlxn"]
                    mCurrentTerm = map["ddlxq"]
                }
                .compose(RxUtils.ioToMain())
                .subscribe({ map: Map<String, String> ->
                    update(false)
                    mView.setYearTermTitle(mCurrentYear, mCurrentTerm)
                    mView.setYearTermData(years, terms)
                    mView.setYearTermOptions(
                            years.indexOf(mCurrentYear),
                            terms.indexOf(mCurrentTerm)
                    )
                }, this::onError)
        )
    }

    override fun updateFromNet() {
        update(true)
    }

    private fun update(showMessage: Boolean) {
        mCompDisposable.add(mModel
                .getScoresFromNet(mCurrentYear, mCurrentTerm)
                .doOnNext {
                    mModel.delete(mCurrentYear, mCurrentTerm)
                    mModel.save(it)
                }
                .compose(RxUtils.singleToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ list: List<Score> ->
                    calcIES(list)
                    calcGPA(list)
                    mView.setRvScoreData(list)
                    if (showMessage) {
                        mView.showMessage(string.score_refresh_successful)
                    }
                }, this::onError)
        )
    }

    override fun switchYearTerm(options1: Int, options2: Int) {
        mCurrentYear = years[options1]
        mCurrentTerm = terms[options2]
        mCompDisposable.add(Observable
                .fromCallable { mModel.getScoresFromDB(mCurrentYear, mCurrentTerm) }
                .flatMap { scores: List<Score> ->
                    if (scores.isEmpty()) {
                        mModel.getScoresFromNet(mCurrentYear, mCurrentTerm)
                                .doOnNext {
                                    mModel.delete(mCurrentYear, mCurrentTerm)
                                    mModel.save(it)
                                }
                    } else {
                        Observable.just(scores)
                    }
                }
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ list: List<Score> ->
                    calcIES(list)
                    calcGPA(list)
                    mView.setIESText(list.size.toString(), "门")
                    mView.setRvScoreData(list)
                }, this::onError)
        )
    }

    override fun openFilterActivity() {
        val intent = Intent(mView.activity, ScoreFilterActivity::class.java)
        intent.putExtra("year", mCurrentYear)
        intent.putExtra("term", mCurrentTerm)
        mView.activity.startActivityForResult(intent, Constant.SCORE_FILTER_ACTIVITY)
    }

    override fun updateIES() {
        calcIES(mModel.getScoresFromDB(mCurrentYear, mCurrentTerm))
    }

    private fun calcIES(scores: List<Score>) {
        mCompDisposable.add(Observable
                .just(scores)
                .map { list: List<Score> ->
                    val ies = GlobalLib.getIES(list)
                    if (ies == 0f) {
                        Pair("0", "分")
                    } else {
                        val result = GlobalLib.formatFloat(ies, 2)
                        val index = result.indexOf('.')
                        if (index == -1) {
                            Pair("result", "分")
                        } else {
                            Pair(result.substring(0, index), result.substring(index) + "分")
                        }
                    }
                }
                .compose(RxUtils.computationToMain())
                .subscribe({ map -> mView.setIESText(map.first, map.second) }, this::onError)
        )
    }

    private fun calcGPA(list: List<Score>) {
        var totalGPA = 0f
        list.forEach {
            totalGPA += (it.gpa ?: 0F)
        }
        val gpa: String = GlobalLib.formatFloat(totalGPA, 2)
        mView.setGPAText(mView.context.getString(string.score_gpa, gpa))
    }

}