package cn.ifafu.ifafu.mvp.score_item

import android.app.Application
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.util.GlobalLib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreItemViewModel(application: Application) : BaseViewModel(application) {

    fun init(id: Long, success: suspend (List<Pair<String, String>>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            if (id == 0L) {
                event.showMessage("查询成绩出错（无法找到ID）")
                return@launch
            }
            val score = mRepository.getScoreById(id)
            if (score == null) {
                event.showMessage("查询成绩出错，Error：无法找到成绩项")
            } else {
                val map: MutableMap<String, String> = LinkedHashMap()
                map["课程名称"] = score.name
                map["成绩"] = if (score.score != -1F) GlobalLib.formatFloat(score.score, 2) + "分" else "无信息"
                map["学分"] = if (score.credit != -1F) GlobalLib.formatFloat(score.credit, 2) + "分" else "无信息"
                map["绩点"] = if (score.gpa != -1F) GlobalLib.formatFloat(score.gpa, 2) + "分" else "无信息"
                map["补考成绩"] = if (score.makeupScore != -1F) GlobalLib.formatFloat(score.makeupScore, 2) + "分" else "无信息"
                map["课程性质"] = score.nature.ifEmpty { "无信息" }
                map["课程属性"] = score.attr.ifEmpty { "无信息" }
                map["开课学院"] = score.institute.ifEmpty { "无信息" }
                map["学年"] = score.year
                map["学期"] = score.term
                map["备注"] = score.remarks
                success(map.toList())
            }
        }
    }


}