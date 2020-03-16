package cn.ifafu.ifafu.ui.main.oldTheme.bean

class ClassPreview(
        val hasInfo: Boolean = false, //是否有下一节课信息
        val message: String = "", //eg:放假中
        val nextClassName: String = "", //课程名称  eg:当前\下一节课：${name}
        val address: String = "", //上课地点
        val numberOfClasses: Array<Int> = arrayOf(0, 0),  //[0]:当前上课节数， [1]:上课总节数
        val isInClass: Boolean = false, //上课中\未上课
        val classTime: String = "", //上课时间段
        val dateText: String,
        val timeLeft: String = "" //剩余上\下课时间
)
