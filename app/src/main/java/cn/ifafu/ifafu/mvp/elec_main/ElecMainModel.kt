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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URLEncoder
import java.util.*

class ElecMainModel internal constructor(context: Context?) : BaseModel(context), ElecMainContract.Model {
    private val service = RetrofitFactory.obtainService(MainService::class.java, null)

    private var elecUser: ElecUser? =  null

    init {
        GlobalScope.launch(Dispatchers.IO) { elecUser =  repository.getElecUser() }
    }

    override fun initCookie(): Observable<Boolean> {
        return Observable.fromCallable {
            val elecCookie = repository.getElecCookie()!!
            service.default2(elecCookie.rescouseType).execute()
            service.page(
                    "31", "3", "2", "", "electricity",
                    URLEncoder.encode("交电费", "gbk"),
                    elecCookie["sourcetypeticket"],
                    SPUtils.get(Constant.SP_ELEC).getString("IMEI"),
                    "0", "1"
            ).execute().body()!!.string().contains("<title>登录</title>")
        }
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

    override fun getSelectionFromJson(): List<Selection> {
        return try {
            val `is` = mContext.assets.open("data.json")
            val isr = BufferedReader(InputStreamReader(`is`))
            var str: String?
            val sb = StringBuilder()
            while (isr.readLine().also { str = it } != null) {
                sb.append(str)
            }
            JSONObject.parseArray(sb.toString(), Selection::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
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