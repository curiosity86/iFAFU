package cn.ifafu.ifafu.mvp.elec_main

import android.app.Application
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.entity.ElecQuery
import cn.ifafu.ifafu.entity.ElecSelection
import cn.ifafu.ifafu.util.ifFalse
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ElecMainViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var selections: List<ElecSelection>
    private lateinit var groupList: List<List<String>>
    private lateinit var elecQuery: ElecQuery

    fun init(setPickerData: (first: List<String>, second: List<List<String>>) -> Unit,
             setPickerOptions: (first: Int, second: Int, building: String, room: String) -> Unit,
             showElecBalance: (String) -> Unit,
             showCardBalance: (String) -> Unit) {
        GlobalScope.launch {
            event.showDialog()
            val elecUser = mRepository.getElecUser()
            if (elecUser != null && mRepository.checkLoginStatus()) {
                val job1 = GlobalScope.launch(Dispatchers.IO) {
                    selections = mRepository.getSelectionList()
                    val group = selections
                            .groupBy { it.group1 }
                            .mapValues { entry ->
                                entry.value.map { it.group2 }.sorted()
                            }
                    val first = group.keys.toList()
                    groupList = group.values.toList()
                    runOnMainThread {
                        setPickerData(group.keys.toList(), groupList)
                    }
                    //初始化电费查询Entity
                    elecQuery = mRepository.getElecQuery().run {
                        if (this != null) {
                            //若数据库存在历史查询记录，则自动填充
                            val selection = selections.find { selection ->
                                selection.buildingId == this.buildingId &&
                                        selection.areaId == this.areaId &&
                                        selection.floorId == this.floorId
                            }
                            if (selection != null) {
                                val firstIndex = first.indexOf(selection.group1)
                                val secondIndex = groupList[firstIndex].indexOf(selection.group2)
                                runOnMainThread {
                                    setPickerOptions(firstIndex, secondIndex, selection.group2, this.room)
                                }
                                val response = mRepository.fetchElectricityInfo(this)
                                if (response.isSuccess) {
                                    showElecBalance(response.body!!)
                                }
                            }
                        }
                        this ?: ElecQuery().apply {
                            account = elecUser.xfbAccount
                            xfbId = elecUser.xfbId
                        }
                    }
                }
                val job2 = innerQueryCardBalance(true, showCardBalance)
                job1.join()
                job2.join()
                event.hideDialog()
            } else {
                if (elecUser != null) {
                    event.showMessage("登录态失效，请重新登录")
                }
                event.hideDialog()
                event.startLoginActivity()
            }
        }
    }

    fun onSelectBuilding(option1: Int, option2: Int, callback: (String) -> Unit) {
        GlobalScope.launch {
            val building = groupList[option1][option2]
            runOnMainThread {
                callback(building)
            }
            val select = selections.find { it.group2 == building }!!
            elecQuery.aid = select.aid
            elecQuery.areaId = select.areaId
            elecQuery.area = select.area
            elecQuery.building = select.building
            elecQuery.buildingId = select.buildingId
            elecQuery.floor = select.floor
            elecQuery.floorId = select.floorId
        }
    }

    fun queryCardBalance(showCardBalance: (String) -> Unit) {
        GlobalScope.launch {
            event.showDialog()
            innerQueryCardBalance(false, showCardBalance).join()
            event.hideDialog()
        }
    }

    fun queryElecBalance(room: String, showBalance: (String) -> Unit) {
        GlobalScope.launch {
            event.showDialog()
            elecQuery.room = room
            val response = mRepository.fetchElectricityInfo(elecQuery)
            if (response.isSuccess) {
                runOnMainThread {
                    showBalance(response.body!!)
                }
                event.showMessage("查询成功")
                event.hideDialog()
                mRepository.saveElecQuery(elecQuery)
            } else if (!mRepository.checkLoginStatus()) {
                event.showMessage("登录态失效，请重新登录")
                event.hideDialog()
                event.startLoginActivity()
            } else {
                event.showMessage("查询出错")
                event.hideDialog()
            }
        }
    }

    private fun innerQueryCardBalance(silent: Boolean, showBalance: (String) -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            val response = mRepository.elecCardBalance()
            if (response.isSuccess) {
                runOnMainThread {
                    showBalance(response.body!!.trimEnd(2))
                }
                silent.ifFalse {
                    event.showMessage("查询成功")
                }
            } else {
                runOnMainThread {
                    showBalance("0")
                }
                event.showMessage(response.message)
            }
        }
    }

}