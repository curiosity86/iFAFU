package cn.ifafu.ifafu.ui.syllabus_setting

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.bean.ZFApiList
import cn.ifafu.ifafu.data.retrofit.APIManager
import retrofit2.awaitResponse

class SyllabusSettingViewModel(application: Application) : BaseViewModel(application) {

    val setting = MutableLiveData<SyllabusSetting>()

    fun init() {
        safeLaunchWithMessage {
            setting.postValue(RepositoryImpl.syllabus.getSetting())
        }
    }

    fun save() {
        safeLaunchWithMessage {
            setting.value?.run {
                RepositoryImpl.syllabus.saveSetting(this)
            }
        }
    }

    fun outputHtml() {
        safeLaunch(block = {
            val user: User? = RepositoryImpl.user.getInUse()
            if (user == null) {
                event.showMessage("用户信息不存在")
                return@safeLaunch
            }
            val url: String = Constant.getUrl(ZFApiList.SYLLABUS, user)
            val referer: String = Constant.getUrl(ZFApiList.MAIN, user)
            val html = APIManager.zhengFangAPI
                    .get(url, referer)
                    .awaitResponse()
                    .body()?.string() ?: ""
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
