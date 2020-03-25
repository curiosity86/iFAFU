package cn.ifafu.ifafu.widget

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
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.activity.SplashActivity
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class SyllabusWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_WIDGET_SYLLABUS_CLICK = "ifafu.widget.syllabus.REFRESH"
    }

    private fun getPendingIntent(context: Context, resId: Int): PendingIntent {
        val intent = Intent()
        intent.setClass(context, SyllabusWidget::class.java)
        intent.data = Uri.parse("Id:$resId")
        intent.action = ACTION_WIDGET_SYLLABUS_CLICK
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    private suspend fun updateSyllabusWidget(context: Context, remoteViews: RemoteViews) = withContext(Dispatchers.IO) {
        remoteViews.setOnClickPendingIntent(R.id.btn_refresh,
                getPendingIntent(context, R.id.btn_refresh))//更新刷新时间
        val format = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
        val now = context.getString(R.string.refresh_time_format, format.format(Date()))
        remoteViews.setTextViewText(R.id.tv_refresh_time, now)
        val repo = RepositoryImpl
        try {
            val courses = repo.syllabus.getAll()
            val setting = repo.syllabus.getSetting()
            val holidayFromToMap = repo.syllabus.getAdjustmentInfo()
            val preview = ClassPreview.convert(courses, holidayFromToMap, setting)
            setPreviewViewInfo(context, remoteViews, preview)
            remoteViews.setOnClickPendingIntent(R.id.btn_go,
                    getPendingIntent(context, R.id.btn_go))
        } catch (e: Exception) {
            remoteViews.setViewVisibility(R.id.message, View.VISIBLE)
            remoteViews.setViewVisibility(R.id.layout_info, View.GONE)
            remoteViews.setTextViewText(R.id.message, e.message)
        }
    }


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        GlobalScope.launch(Dispatchers.Main) {
            appWidgetIds.forEach { appWidgetId ->
                val views = RemoteViews(context.packageName, R.layout.widget_syllabus)
                updateSyllabusWidget(context, views)
                //更新Widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_WIDGET_SYLLABUS_CLICK) {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_syllabus)
            val data = intent.data
            var resId = -1
            if (data != null) {
                resId = Integer.parseInt(data.schemeSpecificPart)
            }
            when (resId) {
                R.id.btn_refresh -> runBlocking {
                    updateSyllabusWidget(context, remoteViews)
                }
                R.id.btn_go -> {
                    val jumpIntent = Intent(context, SplashActivity::class.java)
                    jumpIntent.putExtra("from", Constant.SYLLABUS_WIDGET)
                    jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(jumpIntent)
                }
            }
            val manger = AppWidgetManager.getInstance(context)
            val provider = ComponentName(context, SyllabusWidget::class.java)
            //更新Widget
            manger.updateAppWidget(provider, remoteViews)
        } else {
            super.onReceive(context, intent)
        }
    }

    private fun setPreviewViewInfo(context: Context, remoteViews: RemoteViews, preview: ClassPreview) {
        remoteViews.setTextViewText(R.id.tv_week_time, preview.dateText)
        if (preview.hasInfo) {
            with(remoteViews) {
                //显示详情信息
                setViewVisibility(R.id.message, View.GONE)
                setViewVisibility(R.id.layout_info, View.VISIBLE)
                setTextViewText(R.id.location, preview.address)
                setTextViewText(R.id.classTime, preview.classTime)
                setTextViewText(R.id.timeLeft, preview.timeLeft)
                val numOfClass = preview.numberOfClasses
                setTextViewText(R.id.tv_total, context.getString(R.string.node_format, numOfClass[0], numOfClass[1]))
                if (preview.isInClass) {
                    setTextViewText(R.id.tv_next, context.getString(R.string.now_class_format, preview.nextClass))
                    setTextViewText(R.id.tv_status, context.getString(R.string.in_class))
                    setTextViewCompoundDrawables(R.id.tv_status, R.drawable.ic_point_blue, 0, 0, 0)
                } else {
                    setTextViewText(R.id.tv_next, context.getString(R.string.next_class_format, preview.nextClass))
                    setTextViewText(R.id.tv_status, context.getString(R.string.out_class))
                    setTextViewCompoundDrawables(R.id.tv_status, R.drawable.ic_point_red, 0, 0, 0)
                }
            }
        } else {
            remoteViews.setViewVisibility(R.id.message, View.VISIBLE)
            remoteViews.setViewVisibility(R.id.layout_info, View.GONE)
            remoteViews.setTextViewText(R.id.message, preview.message)
        }
    }
}

