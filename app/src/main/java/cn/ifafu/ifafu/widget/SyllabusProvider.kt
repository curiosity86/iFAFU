package cn.ifafu.ifafu.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.mvp.main.MainActivity

class SyllabusProvider : AppWidgetProvider() {

    private var second = 0

    companion object {
        const val ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
        const val ACTION_ONCLICK = "android.appwidget.action.ONCLICK"
        const val ACTION_AROUTER = "android.appwidget.action.AROUTER"
    }


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        l("onUpdate  ${++second}")
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        l("onAppWidgetOptionsChanged  ${++second}")
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        l("onReceive  ${++second}")
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val appIds: IntArray = appWidgetManager.getAppWidgetIds(ComponentName(context, SyllabusProvider::class.java))

        when (intent?.action) {
            ACTION_APPWIDGET_UPDATE -> {

            }
            ACTION_ONCLICK -> {
                val remoteViews = RemoteViews(context.packageName, R.layout.widget_syllabus)
                remoteViews.setTextViewText(R.id.tv_course_name, ACTION_ONCLICK)
                appWidgetManager.updateAppWidget(appIds, remoteViews)
            }
            ACTION_AROUTER -> {
                val intent1 = Intent(context, MainActivity::class.java)
                context.startActivity(intent1)
            }
        }
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        l("onEnabled  ${++second}")
    }

    override fun onDisabled(context: Context) {
        l("onDisabled  ${++second}")
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        l("onDeleted  ${++second}")
        super.onDeleted(context, appWidgetIds)
    }

    private fun l(msg: String?) {
        Log.d("Widget", "SyllabusProvider@${this.hashCode()} => " + msg)
    }
}