package cn.ifafu.ifafu.experiment.score.filter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.ScoreFilter
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreFilterViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var _filter: ScoreFilter
    private lateinit var _scores: List<Score>

    val scores = MutableLiveData<List<Score>>()
    val ies = MutableLiveData<String>()

    fun init(year: String, term: String) = GlobalScope.launch {
        _filter = RepositoryImpl.ScoreRt.getFilter()
        _scores = RepositoryImpl.ScoreRt.getAll(year, term)
        _scores.forEach {
            it.isIESItem = it.id !in _filter.filterList
        }
        scores.postValue(_scores)
        ies.postValue(_filter.calcIES(_scores).trimEnd(2))
    }

    fun itemChecked(score: Score) {
        GlobalScope.launch(Dispatchers.IO) {
            if (!score.isIESItem) {
                _filter.filterList.add(score.id)
            } else {
                _filter.filterList.removeAll { score.id == it }
            }
            RepositoryImpl.ScoreRt.saveFilter(_filter)
            ies.postValue(_filter.calcIES(_scores).trimEnd(2))
        }
    }

    fun allChecked() = GlobalScope.launch(Dispatchers.IO) {
        _scores.forEach {
            _filter.filterList.remove(it.id)
        }
        RepositoryImpl.ScoreRt.saveFilter(_filter)
        ies.postValue(_filter.calcIES(_scores).trimEnd(2))
    }
}