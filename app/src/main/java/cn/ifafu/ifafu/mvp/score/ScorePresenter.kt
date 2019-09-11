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

class ScorePresenter(view: ScoreContract.View)
    : BaseZFPresenter<ScoreContract.View, ScoreContract.Model>(view, ScoreModel(view.context)), ScoreContract.Presenter {

    private lateinit var years: List<String>
    private lateinit var terms: List<String>
    private lateinit var mCurrentYear: String
    private lateinit var mCurrentTerm: String

    override fun onCreate() {
        mCompDisposable.add(mModel
                .getYearTermList()
                .doOnNext { map: Map<String, List<String>> ->
                    years = map["ddlxn"] ?: error("")
                    terms = map["ddlxq"] ?: error("")
                }
                .flatMap { mModel.getYearTerm() }
                .doOnNext { map: Map<String, String> ->
                    mCurrentYear = map["ddlxn"] ?: error("")
                    mCurrentTerm = map["ddlxq"] ?: error("")
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
                .map {
                    it.ifEmpty { mModel.getScoresFromDB(mCurrentYear, mCurrentTerm) }
                }
                .compose(RxUtils.ioToMain())
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

    override fun switchYearTerm(op1: Int, op2: Int) {
        mCurrentYear = years[op1]
        mCurrentTerm = terms[op2]
        mCompDisposable.add(Observable
                .fromCallable { mModel.getScoresFromDB(mCurrentYear, mCurrentTerm) }
                .flatMap { scores: List<Score> ->
                    if (scores.isEmpty()) {
                        mModel.getScoresFromNet(mCurrentYear, mCurrentTerm)
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
        var totalGPA = 0F
        list.forEach {
            totalGPA += (it.gpa ?: 0F)
        }
        val gpa: String = if (totalGPA == 0F) "无" else GlobalLib.formatFloat(totalGPA, 2)
        mView.setGPAText(mView.context.getString(string.score_gpa, gpa))
    }

    override fun onError(throwable: Throwable?) {
        super.onError(throwable)
        mView.setIESText("0", "分")
        mView.setCntText("0", "门")
        mView.setGPAText("无信息")
    }

    override fun checkIESDetail() {
        val totalScore =  mModel.getScoresFromDB(mCurrentYear, mCurrentTerm)
        val calcJGList = ArrayList<Score>()
        val calcNoJGList = ArrayList<Score>()
        val filterList = ArrayList<Score>()
        totalScore.forEach {
            if (it.isIESItem) {
                if (it.realScore >= 60) {
                    calcJGList.add(it)
                } else {
                    calcNoJGList.add(it)
                }
            } else {
                filterList.add(it)
            }
        }
        var first = true
        val sb = StringBuilder("总共${totalScore.size}门成绩，")
        sb.append("排除任意选修课，体育课，缓考，免修，补修的课程：[")
        filterList.forEach {
            if (first) {
                first = false
            } else {
                sb.append("、")
            }
            sb.append(it.name).append(when {
                it.nature.contains("任意选修") -> "(任意选修课)"
                it.name.contains("体育") -> "(体育课)"
                else -> "(自定义)"
            })
        }
        sb.append("]之外，还有${calcJGList.size + calcNoJGList.size}门纳入计算范围，")
        sb.append("其中共有${calcNoJGList.size}门课程不及格。加权总分为")
        var totalCalcScore = 0F
        first = true
        (calcJGList + calcNoJGList).forEach {
            if (first) {
                first = false
            } else {
                sb.append(" + ")
            }
            sb.append(String.format("%.2f × %.2f", it.realScore, it.credit))
            totalCalcScore += (it.realScore * it.credit)
        }
        sb.append(String.format(" = %.2f，总学分为", totalCalcScore))
        first = true;
        var totalCredit = 0F
        (calcJGList + calcNoJGList).forEach {
            if (first) {
                first = false
            } else {
                sb.append(" + ")
            }
            sb.append(String.format("%.2f", it.credit))
            totalCredit += it.credit
        }
        val ies = totalCalcScore / totalCredit
        sb.append(String.format(" = %.2f，则%.2f / %.2f = %.2f分", totalCredit, totalCalcScore, totalCredit, ies))
        var totalMinus = 0F
        calcNoJGList.forEach {
            totalMinus += it.credit
        }
        sb.append(String.format("，减去不及格的学分共%.2f分，最终为%.2f分。", totalMinus, ies))
        mView.showIESDetail(sb.toString())
    }

}