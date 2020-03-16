package cn.ifafu.ifafu.ui.syllabus_setting

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.bean.ZFApiList
import cn.ifafu.ifafu.data.retrofit.APIManager

class SyllabusSettingViewModel(application: Application) : BaseViewModel(application) {

    val setting = MutableLiveData<SyllabusSetting>()

    fun init() {
        safeLaunchWithMessage {
            setting.postValue(Repository.syllabus.getSetting())
        }
    }

    fun save() {
        safeLaunchWithMessage {
            setting.value?.run {
                Repository.syllabus.saveSetting(this)
            }
        }
    }

    fun outputHtml() {
        safeLaunch(block = {
            val user: User? = Repository.user.getInUse()
            if (user == null) {
                event.showMessage("用户信息不存在")
                return@safeLaunch
            }
            val url: String = School.getUrl(ZFApiList.SYLLABUS, user)
            val referer: String = School.getUrl(ZFApiList.MAIN, user)
            val html = APIManager.zhengFangAPI
                    .getInfo(url, referer)
                    .map { it.string() }
                    .blockingFirst()
            val cm = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("Label", html))
                event.showMessage("测试数据已复制至剪切板")
            } else {
                event.showMessage("获取剪切板失败")
            }
        }, error = {
            event.showMessage(it.message ?: "ERROR")
        })
    }

}
