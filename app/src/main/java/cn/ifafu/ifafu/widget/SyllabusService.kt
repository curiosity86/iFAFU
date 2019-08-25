package cn.ifafu.ifafu.widget

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel
import cn.ifafu.ifafu.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*


class SyllabusService : Service() {

    private var updateDay = 0

    private var courseList: List<Course>? = null
    private var intTime: IntArray? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        l("SyllabusService => onCreate")
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                updateView()
//            }
//        }, 0, 10 * 1000)
    }

    override fun onDestroy() {
        l("onDestroy")
        super.onDestroy()
    }

    private fun l(msg: String?) {
        Log.d("Widget", msg ?: "null")
    }

}