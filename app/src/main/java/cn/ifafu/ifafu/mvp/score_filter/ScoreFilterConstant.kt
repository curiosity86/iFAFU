package cn.ifafu.ifafu.mvp.score_filter

import cn.ifafu.ifafu.base.i.IModel
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.data.entity.Score
import io.reactivex.Observable

class ScoreFilterConstant {

    interface Presenter : IPresenter {

        fun onCheck(score: Score, checked: Boolean)

        fun updateIES()
    }

    interface View : IView {

        fun setAdapterData(list: List<Score>)

        fun setIES(ies: String)

    }

    interface Model : IModel {

        fun getScoresFromDB(year: String, term: String): Observable<List<Score>>

        fun save(score: Score)

    }
}
