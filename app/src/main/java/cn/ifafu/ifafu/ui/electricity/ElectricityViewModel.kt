package cn.ifafu.ifafu.ui.electricity

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.entity.ElecQuery
import cn.ifafu.ifafu.data.bean.ElecSelection
import cn.ifafu.ifafu.data.entity.ElecUser
import cn.ifafu.ifafu.util.ifFalse
import cn.ifafu.ifafu.util.trimEnd
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ElectricityViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var selections: List<ElecSelection>
    private lateinit var groupList: List<List<String>>
    private lateinit var elecQuery: ElecQuery

    val elecUser by lazy { MutableLiveData<ElecUser>() }
    val loginStatus by lazy { MutableLiveData<Boolean>() }
    val verifyBitmap by lazy { MutableLiveData<Bitmap>() }
    val cardBalance by lazy { MutableLiveData<String>() }

    val buildingSelectionList by lazy { MutableLiveData<Pair<List<String>, List<List<String>>>>() }
    val buildingSelected by lazy { MutableLiveData<Pair<Int, Int>>() }
    val roomData by lazy { MutableLiveData<String>() }
    val elecBalance by lazy { MutableLiveData<String>() }

    fun init() {
        GlobalScope.launch {
            event.showDialog()
            var elecUser = Repository.XfbRt.getElecUser()
            when {
                elecUser == null -> { //首次登录
                    elecUser = ElecUser().apply {
                        val user = Repository.user.getInUse()
                        account = user!!.account
                        val account = user.account
                        xfbAccount = if (user.school == Constant.FAFU_JS) "0$account" else account
                    }
                    loginStatus.postValue(false)
                }
                Repository.XfbRt.checkLoginStatus() -> {
                    val job1 = GlobalScope.launch(Dispatchers.IO) {
                        selections = Repository.XfbRt.getSelectionList()
                        val group = selections
                                .groupBy { it.group1 }
                                .mapValues { entry ->
                                    entry.value.map { it.group2 }.sorted()
                                }
                        val first = group.keys.toList()
                        groupList = group.values.toList()
                        buildingSelectionList.postValue(Pair(group.keys.toList(), groupList))
                        //初始化电费查询信息
                        elecQuery = Repository.XfbRt.getElecQuery().run {
                            if (this != null) {
                                //若数据库存在历史查询信息，则自动填充
                                val selection = selections.find { selection ->
                                    selection.buildingId == this.buildingId &&
                                            selection.areaId == this.areaId &&
                                            selection.floorId == this.floorId
                                }
                                if (selection != null) {
                                    val firstIndex = first.indexOf(selection.group1)
                                    val secondIndex = groupList[firstIndex].indexOf(selection.group2)
                                    buildingSelected.postValue(Pair(firstIndex, secondIndex))
                                    roomData.postValue(this.room)
                                    val response = Repository.XfbRt.fetchElectricityInfo(this)
                                    if (response.isSuccess) {
                                        elecBalance.postValue(response.data)
                                    }
                                }
                                this
                            } else {
                                ElecQuery().apply {
                                    account = elecUser.xfbAccount
                                    xfbId = elecUser.xfbId
                                }
                            }
                        }
                    }
                    val job2 = innerQueryCardBalance(true)
                    job1.join()
                    job2.join()
                }
                else -> { //登录态失效
                    event.showMessage("登录态失效，请重新登录")
                    loginStatus.postValue(false)
                }
            }
            runOnMainThread {
                this@ElectricityViewModel.elecUser.value = elecUser
            }
            event.hideDialog()
        }
    }

    fun queryCardBalance() {
        GlobalScope.launch {
            event.showDialog()
            innerQueryCardBalance(false).join()
            event.hideDialog()
        }
    }

    fun queryElecBalance(room: String) {
        GlobalScope.launch {
            event.showDialog()
            val building = groupList[buildingSelected.value?.first
                    ?: 0][buildingSelected.value?.second ?: 0]
            val select = selections.find { it.group2 == building }!!
            elecQuery.aid = select.aid
            elecQuery.areaId = select.areaId
            elecQuery.area = select.area
            elecQuery.building = select.building
            elecQuery.buildingId = select.buildingId
            elecQuery.floor = select.floor
            elecQuery.floorId = select.floorId
            elecQuery.room = room
            val response = Repository.XfbRt.fetchElectricityInfo(elecQuery)
            if (response.isSuccess) {
                elecBalance.postValue(response.data)
                event.showMessage("查询成功")
                Repository.XfbRt.saveElecQuery(elecQuery)
            } else if (!Repository.XfbRt.checkLoginStatus()) {
                event.showMessage("登录态失效，请重新登录")
            } else {
                event.showMessage("查询出错")
            }
            event.hideDialog()
        }
    }

    private fun innerQueryCardBalance(silent: Boolean): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            val response = Repository.XfbRt.elecCardBalance()
            if (response.isSuccess) {
                runOnMainThread {
                    cardBalance.postValue(response.data!!.trimEnd(2))
                }
                silent.ifFalse {
                    event.showMessage("查询成功")
                }
            } else {
                runOnMainThread {
                    cardBalance.postValue("0")
                }
                event.showMessage(response.message)
            }
        }
    }

    fun login(password: String, verify: String) {
        GlobalScope.launch(Dispatchers.IO) {
            event.showDialog()
            val json = JSONObject.parseObject(Repository.XfbRt.elecLogin(elecUser.value?.account
                    ?: "", password, verify))
            if (json.getBoolean("IsSucceed") == true) {
                val obj2 = json.getJSONObject("Obj2")
                elecUser.value?.password = password
                elecUser.value?.xfbId = json.getString("Obj")
                elecUser.value?.run {
                    Repository.XfbRt.saveElecUser(this)
                }
                val elecCookie = Repository.XfbRt.getElecCookie().apply {
                    account = elecUser.value?.account ?: ""
                    rescouseType = obj2.getString("RescouseType")
                }
                Repository.XfbRt.saveElecCookie(elecCookie)
                loginStatus.postValue(true)
            } else {
                if (json.containsKey("Msg")) {
                    event.showMessage(json.getString("Msg"))
                } else {
                    event.showMessage("未知错误")
                }
                refreshVerify()
            }
            event.hideDialog()
        }
    }

    fun refreshVerify() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                verifyBitmap.postValue(Repository.XfbRt.elecVerifyBitmap())
            } catch (e: Exception) {
                event.showMessage(e.errorMessage())
            }
        }
    }

}