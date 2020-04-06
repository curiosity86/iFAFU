package cn.ifafu.ifafu.experiment.ui.score.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.calcIES
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.util.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class ScoreFilterViewModel : ViewModel() {
    private val _scores = MediatorLiveData<List<Score>>()
    private val _ies = MediatorLiveData<String>().apply {
        //监听成绩列表的修改
        addSource(_scores) { scores ->
            this.value = scores.calcIES().toString(2)
        }
    }

    var scores: LiveData<List<Score>> = _scores
    val ies: LiveData<String> = _ies

    fun init(year: String, term: String) = (viewModelScope + Dispatchers.IO).launch {
        _scores.addSource(RepositoryImpl.loadScores(year, term)) {
            _scores.postValue(it)
        }
    }

    fun itemChecked(score: Score) = (viewModelScope + Dispatchers.IO).launch {
        RepositoryImpl.ScoreRt.save(score)
    }

    fun allChecked() = (viewModelScope + Dispatchers.IO).launch {
        val scores = scores.value ?: return@launch
        scores.forEach {
            it.isIESItem = true
        }
        RepositoryImpl.ScoreRt.save(scores)
    }

}