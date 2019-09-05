package cn.ifafu.ifafu.data.entity

class NextCourse (
        var result: Int = 1,
        var title: String = "",
        var name: String = "",
        var address: String = "",
        var nodeText: String = "", //eg: 第1节
        var timeText: String = "", //eg: 10:00-17:00
        var weekText: String = "" //eg: 第1周
) {
    companion object {
        const val IN_HOLIDAY = 1; //放假中
        const val EMPTY_DATA = 2; //无课程信息
        const val NO_TODAY_COURSE = 3; //今天没课
        const val NO_NEXT_COURSE = 4; //课上完了
        const val HAS_NEXT_COURSE = 5; //有下一节课
    }
}