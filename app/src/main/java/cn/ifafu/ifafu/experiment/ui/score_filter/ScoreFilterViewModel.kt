package cn.ifafu.ifafu.experiment.ui.score_filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.experiment.data.repository.ScoreRepository
import cn.ifafu.ifafu.experiment.util.successMap
import cn.ifafu.ifafu.util.GlobalLib.calcIES
import cn.ifafu.ifafu.util.toRadiusString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreFilterViewModel(private val scoreRepository: ScoreRepository) : ViewModel() {

    val scores: LiveData<List<Score>> = scoreRepository.scoreResource.successMap { it }
    val ies: LiveData<String> = scores.map { it.calcIES().toRadiusString(2) }

    fun itemChecked(score: Score) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.saveScore(score)
        }
    }

    fun allChecked() {
        viewModelScope.launch(Dispatchers.IO) {
            val scores = (scores.value ?: return@launch)
                    .onEach { it.isIESItem = true }
            scoreRepository.saveScore(scores)
        }
    }

}