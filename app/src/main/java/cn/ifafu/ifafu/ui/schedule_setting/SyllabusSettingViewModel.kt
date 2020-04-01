package cn.ifafu.ifafu.ui.schedule_setting

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.ZFApiList
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.data.retrofit.APIManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class SyllabusSettingViewModel(application: Application) : BaseViewModel(application) {

    private val repo = RepositoryImpl

    val setting: LiveData<SyllabusSetting> = liveData {
        emit(RepositoryImpl.syllabus.getSetting())
    }

    fun save() = GlobalScope.launch {
        setting.value?.run {
            RepositoryImpl.syllabus.saveSetting(this)
        }
    }

    fun outputHtml() {
        safeLaunch(block = {
            val user: User? = RepositoryImpl.user.getInUse()
            if (user == null) {
                toast("用户信息不存在")
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
                toast("测试数据已复制至剪切板")
            } else {
                toast("获取剪切板失败")
            }
        }, error = {
            toast(it.message ?: "ERROR")
        })
    }

}
