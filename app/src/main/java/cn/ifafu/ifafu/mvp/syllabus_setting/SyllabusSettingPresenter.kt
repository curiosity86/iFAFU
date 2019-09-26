package cn.ifafu.ifafu.mvp.syllabus_setting

import android.app.Activity
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.view.adapter.syllabus_setting.CheckBoxItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.ColorItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.SeekBarItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.TextViewItem
import com.alibaba.fastjson.JSONObject

class SyllabusSettingPresenter(view: SyllabusSettingContract.View)
    : BasePresenter<SyllabusSettingContract.View, SyllabusSettingContract.Model>(view, SyllabusSettingModel(view.context)), SyllabusSettingContract.Presenter {

    private val setting: SyllabusSetting = mModel.getSetting()
    private val settingHash = setting.hashCode()

    override fun onCreate() {
        println(JSONObject.toJSONString(setting))
        mView.initRecycleView(listOf(
                SeekBarItem("一天课程的节数", setting.totalNode, "节", 8, 12) {
                    setting.totalNode = it
                },
                SeekBarItem("总共周数", setting.weekCnt, "周", 18, 24) {
                    setting.weekCnt = it
                },
                SeekBarItem("课程字体大小", setting.textSize, "sp", 8, 18) {
                    setting.textSize = it
                },
                CheckBoxItem("显示水平分割线", "", setting.showHorizontalLine) {
                    setting.showHorizontalLine = it
                },
                CheckBoxItem("显示垂直分割线", "", setting.showVerticalLine) {
                    setting.showVerticalLine = it
                },
                CheckBoxItem("显示上课时间", "", setting.showBeginTimeText) {
                    setting.showBeginTimeText = it
                },
                CheckBoxItem("标题栏深色字体", "", setting.statusDartFont) {
                    setting.statusDartFont = it
                },
                TextViewItem("课表背景", "长按重置为默认背景", { mView.showPicturePicker() }, {
                    setting.background = null
                    mView.showMessage("课表背景已重置")
                }),
                ColorItem("主题颜色", "按钮颜色，文本颜色（除课程文本）", setting.themeColor) { ivColor ->
                    mView.showColorPicker(setting, ivColor)
                }
        ))
    }

    override fun onPictureSelect(uri: String) {
        setting.background = uri
    }

    override fun onFinish() {
        if (setting.hashCode() != settingHash) {
            mModel.save(setting)
            mView.activity.setResult(Activity.RESULT_OK)
        }
    }

}
