package cn.ifafu.ifafu.ui.schedule_setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.experiment.data.repository.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyllabusSettingViewModel(private val courseRepository: CourseRepository) : ViewModel() {

    val setting: LiveData<SyllabusSetting> = courseRepository.setting

    fun save(setting: SyllabusSetting) {
        viewModelScope.launch(Dispatchers.IO) {
            courseRepository.saveSetting(setting)
        }
    }

//    fun outputHtml() {
//        safeLaunch(block = {
//            val user: User? = RepositoryImpl.user.getInUse()
//            if (user == null) {
//                toast("用户信息不存在")
//                return@safeLaunch
//            }
//            val url: String = Constant.getUrl(ZFApiList.SYLLABUS, user)
//            val referer: String = Constant.getUrl(ZFApiList.MAIN, user)
//            val html = APIManager.zhengFangAPI
//                    .get(url, referer)
//                    .awaitResponse()
//                    .body()?.string() ?: ""
//            val cm = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
//            if (cm != null) {
//                cm.setPrimaryClip(ClipData.newPlainText("Label", html))
//                toast("测试数据已复制至剪切板")
//            } else {
//                toast("获取剪切板失败")
//            }
//        }, error = {
//            toast(it.message ?: "ERROR")
//        })
//
//    }

}
