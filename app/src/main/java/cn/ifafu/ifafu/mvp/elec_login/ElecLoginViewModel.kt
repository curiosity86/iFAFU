package cn.ifafu.ifafu.mvp.elec_login

import android.graphics.Bitmap
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.entity.ElecQuery
import cn.ifafu.ifafu.entity.ElecUser
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ElecLoginViewModel(private val repository: Repository) : BaseViewModel() {

    private val elecUser: ElecUser by lazy {
        repository.getElecUser().run {
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

    fun login(account: String, password: String, verify: String, success: suspend (String) -> Unit, fail: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val json = JSONObject.parseObject(repository.elecLogin(account, password, verify))
            if (json.getBoolean("IsSucceed") == true) {
                val obj2 = json.getJSONObject("Obj2")
                elecUser.xfbAccount = account
                elecUser.password = password
                repository.saveElecUser(elecUser)
                val elecCookie = repository.getElecCookie()
                elecCookie.account = elecUser.account
                elecCookie.rescouseType = obj2.getString("RescouseType")
                repository.saveElecCookie(elecCookie)
                val elecQuery = repository.getElecQuery() ?: ElecQuery()
                elecQuery.account = elecUser.account
                elecQuery.xfbId = obj2.getString("ACCOUNT")
                repository.saveElecQuery(elecQuery)
                success("登录成功")
            } else {
                if (json.containsKey("Msg")) {
                    fail(json.getString("Msg"))
                } else {
                    fail("未知错误")
                }
            }
        }
    }

    fun refreshVerify(success: suspend (Bitmap) -> Unit, fail: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                success(repository.elecVerifyBitmap())
            } catch (e: Exception) {
                fail(e.message ?: "ERROR")
            }
        }
    }
}