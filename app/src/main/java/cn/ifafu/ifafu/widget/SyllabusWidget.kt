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
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.ui.activity.SplashActivity
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SyllabusWidget : AppWidgetProvider() {

    private fun updateSyllabusWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_syllabus)
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
        GlobalScope.launch {

        }
    }

    private fun updateView(context: Context, remoteViews: RemoteViews, preview: ClassPreview) {
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
            with(remoteViews) {
                setViewVisibility(R.id.message, View.VISIBLE)
                setViewVisibility(R.id.layout_info, View.GONE)
                setTextViewText(R.id.message, preview.message)
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
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_syllabus)
            val data = intent.data
            var resId = -1
            if (data != null) {
                resId = Integer.parseInt(data.schemeSpecificPart)
            }
            when (resId) {
                R.id.tv_refresh -> updateSyllabusWidget(context, remoteViews)
                R.id.btn_jump -> {
                    GlobalScope.launch {
                        val jumpIntent: Intent
                        when {
                            Repository.user.getInUse() == null -> {
                                jumpIntent = Intent(context, SplashActivity::class.java)
                            }
                            IFAFU.FIRST_START_APP -> {
                                jumpIntent = Intent(context, SplashActivity::class.java)
                                jumpIntent.putExtra("jump", Constant.ACTIVITY_SYLLABUS)
                            }
                            else -> jumpIntent = Intent(context, SyllabusActivity::class.java)
                        }
                        jumpIntent.putExtra("from", Constant.SYLLABUS_WIDGET)
                        jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        GlobalScope.launch(Dispatchers.Main) {
                            context.startActivity(jumpIntent)
                        }
                    }
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
        private const val ACTION_WIDGET_SYLLABUS_CLICK = "ifafu.widget.syllabus.REFRESH"
    }

}

