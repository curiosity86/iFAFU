package cn.ifafu.ifafu.view.adapter.syllabus_setting

data class SeekBarItem(
        val id: Int,
        val title: String,
        var value: Int,
        val unit: String,
        val minValue: Int,
        val maxValue: Int
) : BaseSettingItem()
