package cn.ifafu.ifafu.mvp.elec_login

import android.app.Application
import android.graphics.Bitmap
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.entity.ElecUser
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ElecLoginViewModel(application: Application) : BaseViewModel(application) {

    private val elecUser: ElecUser by lazy {
        mRepository.getElecUser().run {
            if (this == null) {
                val elecUser = ElecUser()
                val user = Repository.getInUseUser()
                elecUser.account = user!!.account
                val account = user.account
                if (user.schoolCode == School.FAFU_JS) {
                    elecUser.xfbAccount = "0$account"
                } else {
                    elecUser.xfbAccount = account
                }
                elecUser
            } else {
                this
            }
        }
    }

    fun init(callback: suspend (account: String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            callback(elecUser.account)
        }
    }

    fun login(account: String, password: String, verify: String, success: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            event.showDialog()
            val json = JSONObject.parseObject(mRepository.elecLogin(account, password, verify))
            if (json.getBoolean("IsSucceed") == true) {
                val obj2 = json.getJSONObject("Obj2")
                elecUser.xfbAccount = account
                elecUser.password = password
                elecUser.xfbId = json.getString("Obj")
                mRepository.saveElecUser(elecUser)
                val elecCookie = mRepository.getElecCookie()
                elecCookie.account = elecUser.account
                elecCookie.rescouseType = obj2.getString("RescouseType")
                mRepository.saveElecCookie(elecCookie)
                success("登录成功")
            } else {
                if (json.containsKey("Msg")) {
                    event.showMessage(json.getString("Msg"))
                } else {
                    event.showMessage("未知错误")
                }
            }
            event.hideDialog()
        }
    }

    fun refreshVerify(success: suspend (Bitmap) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                success(mRepository.elecVerifyBitmap())
            } catch (e: Exception) {
                event.showMessage(e.errorMessage())
            }
        }
    }
}