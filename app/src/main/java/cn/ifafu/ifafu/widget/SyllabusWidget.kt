package cn.ifafu.ifafu.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.ui.schedule.SyllabusActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SyllabusWidget : AppWidgetProvider() {

    companion object;

    private suspend fun updateSyllabusWidget(context: Context, remoteViews: RemoteViews) = withContext(Dispatchers.IO) {
        //设置跳转课表按钮监听
        val syllabusIntent: PendingIntent = Intent(context, SyllabusActivity::class.java)
                .let { intent ->
                    intent.putExtra("from", Constant.SYLLABUS_WIDGET)
                    PendingIntent.getActivity(context, 0, intent, 0)
                }
        remoteViews.setOnClickPendingIntent(R.id.btn_go, syllabusIntent)
        //设置刷新Widget按钮监听
        val refreshIntent: PendingIntent = Intent()
                .let { intent ->
                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    PendingIntent.getBroadcast(context, 0, intent, 0)
                }
        remoteViews.setOnClickPendingIntent(R.id.btn_refresh, refreshIntent)
        //更新刷新时间
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
        } catch (e: Exception) {
            remoteViews.setViewVisibility(R.id.message, View.VISIBLE)
            remoteViews.setViewVisibility(R.id.layout_info, View.GONE)
            remoteViews.setTextViewText(R.id.message, e.message)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_syllabus)
            runBlocking {
                updateSyllabusWidget(context, views)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_syllabus)
            runBlocking {
                updateSyllabusWidget(context, remoteViews)
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

