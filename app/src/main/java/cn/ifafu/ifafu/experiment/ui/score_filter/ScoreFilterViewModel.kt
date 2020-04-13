package cn.ifafu.ifafu.experiment.ui.score_filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.experiment.data.repository.ScoreRepository
import cn.ifafu.ifafu.experiment.util.successMap
import cn.ifafu.ifafu.util.toRadiusString
import kotlinx.coroutines.launch

class ScoreFilterViewModel(private val scoreRepository: ScoreRepository) : ViewModel() {

    val scores: LiveData<List<Score>> = scoreRepository.scoreResource.successMap { it }

    val ies: LiveData<String> = scoreRepository.ies.map { it.toRadiusString(2) }

    fun itemChecked(score: Score) = viewModelScope.launch {
        scoreRepository.saveScore(score)
    }

    fun allChecked() = viewModelScope.launch {
        val scores = (scores.value ?: return@launch)
                .onEach { it.isIESItem = true }
        scoreRepository.saveScore(scores)
    }

}