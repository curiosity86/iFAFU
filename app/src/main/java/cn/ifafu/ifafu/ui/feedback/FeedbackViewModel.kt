package cn.ifafu.ifafu.ui.feedback

import android.app.Application
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.util.HttpUtils
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedbackViewModel(application: Application) : BaseViewModel(application) {

    fun submit(contact: String, message: String) {
        GlobalScope.launch {
            if (message.isEmpty()) {
                event.showMessage("请输入反馈内容");
            }
            val resp = HttpUtils.post(url = "http://woolsen.cn/feedback", body = mapOf(
                    "sno" to Repository.user.getInUseAccount(),
                    "contact" to contact,
                    "message" to message
            ))
            if (resp.isSuccessful && resp.body() != null) {
                val jo = JSONObject.parseObject(resp.body()?.string())
                if (jo.getIntValue("code") == 200) {
                    event.showMessage("反馈成功，iFAFU将会第一时间处理");
                } else {
                    event.showMessage(jo.getString("message"));
                }
            } else {
                event.showMessage("反馈提交出错");
            }
        }
    }

}