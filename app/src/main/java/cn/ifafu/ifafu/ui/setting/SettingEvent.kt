package cn.ifafu.ifafu.ui.setting

sealed class SettingEvent(val id: Int) {
    class OnDrag(id: Int, val progress: Int) : SettingEvent(id)
    class OnClick(id: Int) : SettingEvent(id)
    class OnLongClick(id: Int) : SettingEvent(id)
    class OnCheck(id: Int, val checked: Boolean) : SettingEvent(id)
}