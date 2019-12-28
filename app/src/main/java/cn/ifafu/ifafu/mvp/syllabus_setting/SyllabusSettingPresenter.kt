package cn.ifafu.ifafu.mvp.syllabus_setting

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.base.addDisposable
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.entity.SyllabusSetting
import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.entity.ZFApiList
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.view.adapter.syllabus_setting.CheckBoxItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.ColorItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.SeekBarItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.TextViewItem
import io.reactivex.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SyllabusSettingPresenter(view: SyllabusSettingContract.View)
    : BasePresenter<SyllabusSettingContract.View, SyllabusSettingContract.Model>(view, SyllabusSettingModel(view.context)), SyllabusSettingContract.Presenter {

    private lateinit var setting: SyllabusSetting
    private var settingHash: Int = 0

    override fun onCreate() {
        addDisposable {
            Observable.fromCallable {
                setting = mModel.getSetting()
                settingHash = setting.hashCode()
                listOf(
                        CheckBoxItem("是否使用网络解析", "通过服务器解析，课表出问题就选它，然后刷新课表", setting.parseType == 2) {
                            setting.parseType = if (it) 2 else 1
                        },
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
                            mView.showPicturePicker()
                        }, {
                            setting.background = ""
                            mView.showMessage("课表背景已重置")
                        }),
                        ColorItem("主题颜色", "按钮颜色，文本颜色（除课程文本）", setting.themeColor) { ivColor ->
                            mView.showColorPicker(setting, ivColor)
                        },
                        TextViewItem("导出测试数据到剪切板", "", {
                            GlobalScope.launch {
                                val user: User? = Repository.getInUseUser()
                                if (user == null) {
                                    mView.showMessage("用户信息不存在")
                                    return@launch
                                }
                                val url: String = School.getUrl(ZFApiList.SYLLABUS, user)
                                val referer: String = School.getUrl(ZFApiList.MAIN, user)
                                addDisposable {
                                    APIManager.getZhengFangAPI()
                                            .getInfo(url, referer)
                                            .map { it.string() }
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
                                }
                            }
                        }, {})
                )
            }
                    .compose(RxUtils.ioToMain())
                    .subscribe({
                        mView.initRecycleView(it)
                    }, this::onError)
        }
    }

    override fun onPictureSelect(uri: String) {
        setting.background = uri
    }

    override suspend fun onFinish() {
        mModel.save(setting)
        if (setting.hashCode() != settingHash) {
            mView.activity.setResult(Activity.RESULT_OK)
        }
    }

}
