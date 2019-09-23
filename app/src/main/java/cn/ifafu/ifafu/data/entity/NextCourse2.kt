package cn.ifafu.ifafu.data.entity

class NextCourse2 (
        result: Int = 1,
        title: String = "",
        name: String = "",
        address: String = "",
        node: Int = 0,
        var totalNode: Int = 0,
        timeText: String = "", //eg: 10:00-17:00
        var lastText: String = ""
): NextCourse(result, title, name, address, node, timeText, "") {
    companion object {
        const val IN_COURSE = 9; //上课中
    }
}