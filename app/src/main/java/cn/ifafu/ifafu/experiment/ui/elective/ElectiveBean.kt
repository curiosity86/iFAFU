package cn.ifafu.ifafu.experiment.ui.elective

import cn.ifafu.ifafu.data.entity.Score

class ElectiveBean(
        var category: String,
        var statistics: String,
        var scores: List<Score>,
        var done: Boolean
)