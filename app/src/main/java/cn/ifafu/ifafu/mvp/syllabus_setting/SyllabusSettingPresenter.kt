package cn.ifafu.ifafu.mvp.syllabus_setting

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.local.RepositoryImpl
import cn.ifafu.ifafu.util.RxUtils
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
                TextViewItem("课表背景", "长按重置为默认背景", {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        mView.showMessage("AndroidQ暂不支持自定义背景，方法还在研究中~")
                    } else {
                        mView.showPicturePicker()
                    }
                }, {
                    setting.background = null
                    mView.showMessage("课表背景已重置")
                }),
                ColorItem("主题颜色", "按钮颜色，文本颜色（除课程文本）", setting.themeColor) { ivColor ->
                    mView.showColorPicker(setting, ivColor)
                },
                TextViewItem("导出测试数据到剪切板", "", {
                    val user: User? = RepositoryImpl.getInstance().loginUser
                    val url: String = School.getUrl(ZhengFang.SYLLABUS, user) ?: ""
                    val referer: String = School.getUrl(ZhengFang.MAIN, user) ?: ""
                    APIManager.getZhengFangAPI()
                            .getInfo(url, referer)
                            .map {
//                                val doc = Jsoup.parse(it.string())
//                                val table = doc.getElementById("Table1")
//                                table.outerHtml()
                                it.string()
                            }
                            .compose(RxUtils.ioToMain())
                            .subscribe({
                                val cm = mView.activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                                if (cm != null) {
                                    cm.setPrimaryClip(ClipData.newPlainText("Label", it))
                                    mView.showMessage("测试数据已复制至剪切板")
                                } else {
                                    mView.showMessage("导出失败")
                                }
                            }, {
                                mView.showMessage(it.message ?: "ERROR")
                            })
                }, {})
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
