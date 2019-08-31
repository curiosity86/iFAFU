package cn.ifafu.ifafu.view.adapter.syllabus_setting

data class CheckBoxItem(
        val id: Int,
        val title: String,
        var checked: Boolean
): BaseSettingItem()