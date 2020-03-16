package cn.ifafu.ifafu.data.bean

import cn.ifafu.ifafu.data.entity.Score

class ElectiveInfo (
        var title: String,
        var subtitle: String,
        var scores: List<Score>,
        var done: Boolean
)