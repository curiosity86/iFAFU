package cn.ifafu.ifafu.mvp.elec_main

import android.content.Context
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseModel
import cn.ifafu.ifafu.data.http.elec.MainService
import cn.ifafu.ifafu.data.http.elec.RetrofitFactory
import cn.ifafu.ifafu.entity.ElecQuery
import cn.ifafu.ifafu.entity.ElecUser
import cn.ifafu.ifafu.entity.Selection
import cn.ifafu.ifafu.util.SPUtils
import com.alibaba.fastjson.JSONObject
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.*

class ElecMainModel internal constructor(context: Context?) : BaseModel(context), ElecMainContract.Model {
    private val service = RetrofitFactory.obtainService(MainService::class.java, null)

    private var elecUser: ElecUser? = null

    init {
        GlobalScope.launch(Dispatchers.IO) { elecUser = repository.getElecUser() }
    }

    override fun initCookie(): String {
        val elecCookie = repository.getElecCookie()!!
        service.default2(elecCookie.rescouseType).execute()
        return service.page(
                    "31", "3", "2", "", "electricity",
                    URLEncoder.encode("交电费", "gbk"),
                    elecCookie["sourcetypeticket"],
                    SPUtils.get(Constant.SP_ELEC).getString("IMEI"),
                    "0", "1"
            ).execute().body()!!.string()
    }

    override fun queryElectricity(data: ElecQuery): Observable<String> {
        return Observable.fromCallable {
            val responseBody = service.query(
                    data.toFiledMap(ElecQuery.Query.ROOMINFO)
            ).execute().body()
            JSONObject.parseObject(responseBody!!.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_elec_roominfo")
                    .getString("errmsg")
        }
    }

    override fun elecPay(data: ElecQuery, price: String): Observable<String> {
        return Observable.fromCallable {
            val responseBody = service.elecPay(
                    "http://cardapp.fafu.edu.cn:8088/PPage/ComePage",
                    "###", "1", data.aid,
                    data.xfbId, price, data.room, data.room,
                    data.floorId, data.floor,
                    data.buildingId, data.building,
                    data.areaId, data.area, "true"
            ).execute().body()
            val jo = JSONObject.parseObject(responseBody!!.string())
                    .getJSONObject("Msg")
            if (jo.containsKey("pay_elec_gdc")) {
                return@fromCallable jo.getJSONObject("pay_elec_gdc")
                        .getString("errmsg")
            } else {
                return@fromCallable jo.toJSONString()
            }
        }
    }

    override fun getSelection(): List<Selection> {
        return listOf(
                Selection("0030000000002501", "常工电子电控", 1, listOf(
                        Selection("农林大学", "农林大学", 2, listOf(
                                Selection("1", "北区1号楼"),
                                Selection("2", "北区2号楼"),
                                Selection("953", "南区10号楼"),
                                Selection("954", "南区3号楼"),
                                Selection("955", "南区4号楼"),
                                Selection("956", "桃山3号楼")
                        ))
                )),
                Selection("0030000000008001", "山东科大电子电控", 2, listOf(
                        Selection("桃山区", "桃山区", 3, listOf(
                                Selection("7#", "7#"),
                                Selection("8#", "8#")
                        ))
                )),
                Selection("0030000000008101", "开普电子电控", 1, listOf(
                        Selection("0", "本校区", 2, listOf(
                                Selection("7", "下安5号"),
                                Selection("3", "下安1号"),
                                Selection("10", "北区4号"),
                                Selection("1", "南区2号"),
                                Selection("9", "北区3号"),
                                Selection("11", "北区5号"),
                                Selection("6", "下安4号"),
                                Selection("2", "南区1号"),
                                Selection("8", "下安6号"),
                                Selection("5", "下安2号")
                        ))
                )),
                Selection("0030000000008102", "开普电控东苑", 2, listOf(
                        Selection("1", "东苑1#楼"),
                        Selection("5", "东苑2#楼"),
                        Selection("6", "东苑3#楼"),
                        Selection("7", "东苑4#楼"),
                        Selection("8", "东苑5#楼"),
                        Selection("9", "东苑6#楼"),
                        Selection("10", "东苑7#楼"),
                        Selection("11", "东苑8#楼")
                ))
        )
    }

    override fun queryDKInfos(): Observable<Map<String, String>> {
        return Observable.create { emitter: ObservableEmitter<Map<String, String>> ->
            val responseBody = service.query(
                    "{\"query_applist\":{ \"apptype\": \"elec\" }}",
                    "synjones.onecard.query.applist",
                    "true"
            ).execute().body()
            val ctrlList = JSONObject.parseObject(responseBody!!.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_applist")
                    .getJSONArray("applist")
            val aidMap: MutableMap<String, String> = LinkedHashMap()
            for (o in ctrlList) {
                val jo = o as JSONObject
                aidMap[jo.getString("name")] = jo.getString("aid")
            }
            emitter.onNext(aidMap)
            emitter.onComplete()
        }
    }

    override fun queryBalance(): Observable<Double> {
        return Observable.create { emitter: ObservableEmitter<Double> ->
            val responseBody = service.queryBalance(
                    "true"
            ).execute().body()
            val msg = JSONObject.parseObject(responseBody!!.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_card")
                    .getJSONArray("card")
                    .getJSONObject(0)
            val total = (msg.getIntValue("db_balance") + msg.getIntValue("unsettle_amount")) / 100.0
            emitter.onNext(total)
            emitter.onComplete()
        }
    }

    override fun save(data: ElecQuery) {
        repository.saveElecQuery(data)
    }

    override fun getQueryData(): ElecQuery? {
        return repository.getElecQuery()
    }

}