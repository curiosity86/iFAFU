package cn.ifafu.ifafu.ui.feedback

import android.app.Application
import android.view.View
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.util.HttpClient
import cn.woolsen.easymvvm.binding.OnClickCommand
import cn.woolsen.easymvvm.livedata.LiveDataString
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class FeedbackViewModel(application: Application) : BaseViewModel(application) {

    val contact = LiveDataString()
    val content = LiveDataString()
    val toastMessage = LiveDataString()

    val submitCommand = object : OnClickCommand {
        override fun onClick(view: View) {
            submit()
        }
    }

    private fun submit() {
        GlobalScope.launch {
            val message = content.value
            if (message.isNullOrBlank()) {
                event.showMessage("请输入反馈内容")
                return@launch
            }
            try {
                val resp = HttpClient().post(url = "http://woolsen.cn/feedback", body = mapOf(
                        "sno" to RepositoryImpl.user.getInUseAccount(),
                        "contact" to (contact.value ?: ""),
                        "message" to message
                ))
                if (resp.isSuccessful && resp.body() != null) {
                    val jo = JSONObject.parseObject(resp.body()?.string())
                    if (jo.getIntValue("code") == 200) {
                        toastMessage.postValue("感谢小伙伴的反馈~\niFAFU会第一时间处理")
                    } else {
                        toastMessage.postValue(jo.getString("message"))
                    }
                } else {
                    toastMessage.postValue("反馈提交出错，请加QQ群反馈")
                }
            } catch (e: IOException) {
                toastMessage.postValue("网络异常")
            }
        }
    }
}