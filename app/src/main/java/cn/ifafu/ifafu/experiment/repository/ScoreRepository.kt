package cn.ifafu.ifafu.experiment.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.ScoreFilter
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.db.ScoreDao
import cn.ifafu.ifafu.experiment.db.ZFService

open class ScoreRepository(
        user: User,
        private val dao: ScoreDao,
        private val service: ZFService
) {

    private val user = MutableLiveData(user)

    fun loadFilter(): LiveData<ScoreFilter> =
            user.switchMap { user -> dao.getFilter(user.account) }

    fun loadScore(id: Long): LiveData<Score> =
            user.switchMap { user -> dao.getScore(user.account, id) }

    fun loadScores(): LiveData<List<Score>> =
            user.switchMap { user -> dao.getAllScores(user.account) }

    fun loadScores(year: String, term: String) =
            user.switchMap { user -> dao.getAllScores(user.account, year, term) }

    fun saveFilter(filter: ScoreFilter) {
        dao.saveFilter(filter)
    }

}