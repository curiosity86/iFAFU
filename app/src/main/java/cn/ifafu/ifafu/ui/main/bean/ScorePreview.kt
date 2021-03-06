package cn.ifafu.ifafu.ui.main.bean

import cn.ifafu.ifafu.data.entity.Score

class ScorePreview private constructor(
        val hasInfo: Boolean,
        val message: String = "",
        val text: String = ""
) {
    companion object {
        fun convert(scores: List<Score>?): ScorePreview {
            return when {
                scores == null ->
                    ScorePreview(hasInfo = false, message = "获取成绩失败")
                scores.isEmpty() ->
                    ScorePreview(hasInfo = false, message = "暂无成绩信息")
                else ->
                    ScorePreview(hasInfo = true, text = "已出${scores.size}门成绩")
            }
        }
    }
}