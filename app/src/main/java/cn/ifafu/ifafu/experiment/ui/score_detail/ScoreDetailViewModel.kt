package cn.ifafu.ifafu.experiment.ui.score_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.experiment.data.repository.ScoreRepository
import cn.ifafu.ifafu.util.toRadiusString
import kotlinx.coroutines.Dispatchers
import kotlin.collections.set

class ScoreDetailViewModel(id: Int, scoreRepository: ScoreRepository) : ViewModel() {

    val scoreMap = scoreRepository.scoreResource.switchMap { scores ->
        liveData(Dispatchers.IO) {
            if (scores !is Resource.Success) {
                return@liveData
            }
            val score = scores.data.find { it.id == id } ?: return@liveData
            val map: MutableMap<String, String> = linkedMapOf()
            map["课程名称"] = score.name
            map["成绩"] = if (score.score != -1F) score.score.toRadiusString(2) + "分" else "无信息"
            map["学分"] = if (score.credit != -1F) score.credit.toRadiusString(2) + "分" else "无信息"
            map["绩点"] = if (score.gpa != -1F) score.gpa.toRadiusString(2) + "分" else "无信息"
            map["补考成绩"] = if (score.makeupScore != -1F) score.makeupScore.toRadiusString(2) + "分" else "无信息"
            map["课程性质"] = score.nature.ifEmpty { "无信息" }
            map["课程属性"] = score.attr.ifEmpty { "无信息" }
            map["开课学院"] = score.institute.ifEmpty { "无信息" }
            map["学年"] = score.year
            map["学期"] = score.term
            map["备注"] = score.remarks
            emit(map.toList())
        }
    }

}