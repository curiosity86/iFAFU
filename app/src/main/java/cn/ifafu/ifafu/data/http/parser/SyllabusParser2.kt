package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.User
import com.alibaba.fastjson.JSONObject
import io.reactivex.Observable
import io.reactivex.ObservableSource
import okhttp3.ResponseBody
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by woolsen on 19/11/23
 */
class SyllabusParser2(user: User? = null) : BaseParser<MutableList<Course>>() {

    private val account: String = user?.account ?: "null"

    override fun parse(html: String): MutableList<Course> {
        val jsonArray = JSONObject.parseArray(html)
        val courses = ArrayList<Course>()
        for (i in 0 until jsonArray.size) {
            val jo = jsonArray.getJSONObject(i)
            val course = Course()
            course.name = jo.getString("name")
            course.address = jo.getString("address")
            course.teacher = jo.getString("teacher")
            course.weekday = (jo.getInteger("weekday") % 7) + 1
            course.weekSet = TreeSet(jo.getJSONArray("weekSet").toJavaList(Int::class.java))
            course.beginNode = jo.getInteger("beginNode")
            course.nodeCnt = jo.getInteger("nodeLength")
            courses.add(course)
        }
        return courses.apply {
            forEach {
                it.account = account
                it.id = it.hashCode().toLong()
            }
        }
    }

    override fun apply(upstream: Observable<ResponseBody>): ObservableSource<MutableList<Course>> {
        return upstream.map { responseBody ->
            parse(responseBody.string())
        }
    }
}
