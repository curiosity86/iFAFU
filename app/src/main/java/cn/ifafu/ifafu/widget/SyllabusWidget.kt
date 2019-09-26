package cn.ifafu.ifafu.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.entity.NextCourse
import cn.ifafu.ifafu.data.local.RepositoryImpl
import cn.ifafu.ifafu.mvp.main.main2.Main2Model
import cn.ifafu.ifafu.mvp.other.SplashActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import java.text.SimpleDateFormat
import java.util.*

class SyllabusWidget : AppWidgetProvider() {

    private fun updateSyllabusWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.syllabus_widget)
        updateSyllabusWidget(context, views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingIntent(context: Context, resId: Int): PendingIntent {
        val intent = Intent()
        intent.setClass(context, SyllabusWidget::class.java)
        intent.data = Uri.parse("Id:$resId")
        intent.action = ACTION_WIDGET_SYLLABUS_CLICK
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    @SuppressLint("DefaultLocale", "CheckResult")
    private fun updateSyllabusWidget(context: Context, remoteViews: RemoteViews) {

        remoteViews.setOnClickPendingIntent(R.id.tv_refresh,
                getPendingIntent(context, R.id.tv_refresh))
        remoteViews.setOnClickPendingIntent(R.id.btn_jump,
                getPendingIntent(context, R.id.btn_jump))
        val format = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
        remoteViews.setTextViewText(R.id.tv_refresh_time, context.getString(R.string.refresh_time_format, format.format(Date())))

        Main2Model(context).getNextCourse()
                .subscribe({
                    updateView(remoteViews, it)
                }, {
                    remoteViews.setViewVisibility(R.id.tv_null, View.VISIBLE)
                    remoteViews.setViewVisibility(R.id.layout_info, View.GONE)
                    if (it is NullPointerException) {
                        remoteViews.setTextViewText(R.id.tv_null, "暂无课表信息")
                    } else {
                        remoteViews.setTextViewText(R.id.tv_null, it.message)
                    }
                })
    }

    private fun updateView(remoteViews: RemoteViews, next: NextCourse) {
        remoteViews.setTextViewText(R.id.tv_week_time, next.dateText)
        when (next.result) {
            NextCourse.HAS_NEXT_COURSE, NextCourse.IN_COURSE -> {
                remoteViews.setViewVisibility(R.id.tv_null, View.GONE)
                remoteViews.setViewVisibility(R.id.layout_info, View.VISIBLE)
                remoteViews.setTextViewText(R.id.tv_next, next.title + next.name)
                remoteViews.setTextViewText(R.id.tv_location, next.address)
                remoteViews.setTextViewText(R.id.tv_time, "第${next.node}节 ${next.timeText}")
                remoteViews.setTextViewText(R.id.tv_last, next.lastText)
                remoteViews.setTextViewText(R.id.tv_total, "今日:第${next.node}/${next.totalNode}节")
                if (next.result == NextCourse.IN_COURSE) {
                    remoteViews.setTextViewText(R.id.tv_status, "上课中")
                    remoteViews.setTextViewCompoundDrawables(R.id.tv_status, R.drawable.ic_point_blue, 0, 0, 0)
                } else {
                    remoteViews.setTextViewText(R.id.tv_status, "未上课")
                    remoteViews.setTextViewCompoundDrawables(R.id.tv_status, R.drawable.ic_point_red, 0, 0, 0)
                }
            }
            else -> {
                remoteViews.setViewVisibility(R.id.tv_null, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.layout_info, View.GONE)
                remoteViews.setTextViewText(R.id.tv_null, next.title)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateSyllabusWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == null) {
            return
        }
        if (intent.action == ACTION_WIDGET_SYLLABUS_CLICK) {
            val remoteViews = RemoteViews(context.packageName, R.layout.syllabus_widget)
            val data = intent.data
            var resId = -1
            if (data != null) {
                resId = Integer.parseInt(data.schemeSpecificPart)
            }
            when (resId) {
                R.id.tv_refresh -> updateSyllabusWidget(context, remoteViews)
                R.id.btn_jump -> {
                    val jumpIntent: Intent
                    if (IFAFU.FIRST_START_APP) {
                        jumpIntent = Intent(context, SplashActivity::class.java)
                        jumpIntent.putExtra("jump", Constant.ACTIVITY_SYLLABUS)
                    } else if (RepositoryImpl.getInstance().loginUser == null) {
                        jumpIntent = Intent(context, SplashActivity::class.java)
                    } else {
                        jumpIntent = Intent(context, SyllabusActivity::class.java)
                    }
                    jumpIntent.putExtra("from", Constant.SYLLABUS_WIDGET)
                    jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(jumpIntent)
                }
            }
            val manger = AppWidgetManager.getInstance(context)
            val thisName = ComponentName(context, SyllabusWidget::class.java)
            manger.updateAppWidget(thisName, remoteViews)
        } else {
            super.onReceive(context, intent)
        }
    }

    companion object {

        private val ACTION_WIDGET_SYLLABUS_CLICK = "ifafu.widget.syllabus.REFRESH"
    }

}

