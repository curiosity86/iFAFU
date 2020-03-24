package cn.ifafu.ifafu.ui.syllabus.view

class CourseItem(
        var id: Long,
        var name: String, //课程名称
        var address: String, //上课地点
        @DayOfWeek
        var dayOfWeek: Int, //星期
        var startNode: Int, //开始节数
        var nodeCount: Int, //上课节数
        var color: Int = -1 //显示颜色
)
