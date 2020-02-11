package cn.ifafu.ifafu.data.entity

class ElecSelection(
    val aid: String,
    val name: String,
    val areaId: String = "",
    val area: String = "",
    val buildingId: String = "",
    val building: String = "",
    val floorId: String = "",
    val floor: String = "",

    val group1: String,
    val group2: String
)