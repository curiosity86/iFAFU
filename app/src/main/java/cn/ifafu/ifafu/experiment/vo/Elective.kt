package cn.ifafu.ifafu.experiment.vo

import cn.ifafu.ifafu.data.entity.Score

class Elective (
        var category: String,
        var statistics: String,
        var scores: List<Score>,
        var done: Boolean
)