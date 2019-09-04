package cn.ifafu.ifafu.view.adapter.syllabus_setting

data class SeekBarItem(
        val title: String,
        var value: Int,
        val unit: String,
        val minValue: Int,
        val maxValue: Int,
        val listener: (Int) -> Unit
) : SettingItem()
