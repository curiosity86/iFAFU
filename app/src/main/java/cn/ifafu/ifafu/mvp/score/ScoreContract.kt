package cn.ifafu.ifafu.mvp.score

import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.YearTerm
import cn.ifafu.ifafu.mvp.base.i.IView
import cn.ifafu.ifafu.mvp.base.i.IZFModel
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter
import io.reactivex.Observable

class ScoreContract {
    interface View : IView {
        fun showIESDetail(text: String)
        fun setYearTermOptions(option1: Int, option2: Int)
        fun setRvScoreData(data: List<Score>)
        fun setYearTermData(years: List<String>, terms: List<String>)
        fun setYearTermTitle(year: String, term: String)
        fun setIESText(big: String, little: String)
        fun setCntText(big: String, little: String)
        fun setGPAText(text: String)
    }

    interface Presenter : IZFPresenter {
        fun updateFromNet()
        fun switchYearTerm(op1: Int, op2: Int)
        fun openFilterActivity()
        fun updateIES()
        fun checkIESDetail()
    }

    interface Model : IZFModel {
        /**
         * 获取成绩并保存
         * @param year 学年
         * @param term 学期
         * @return 成绩
         */
        fun getScoresFromNet(year: String, term: String): Observable<List<Score>>

        fun getScoresFromNet(): Observable<MutableList<Score>>

        fun getScoresFromDB(year: String, term: String): List<Score>

        fun getYearTermList(): Observable<YearTerm>

        fun getYearTerm(): Observable<Map<String, String>>
    }
}