package cn.ifafu.ifafu.view.adapter.syllabus_setting

data class CheckBoxItem(
        val title: String,
        var checked: Boolean,
        val listener: (checked: Boolean) -> Unit
) : SettingItem()