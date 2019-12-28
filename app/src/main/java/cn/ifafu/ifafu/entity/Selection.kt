package cn.ifafu.ifafu.entity

data class Selection(
    var id: String = "",
    var name: String = "",
    var next: Int = 0,
    var data: List<Selection> = ArrayList()
)