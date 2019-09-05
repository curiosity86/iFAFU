package cn.ifafu.ifafu.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.NextCourse;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel;

public class SyllabusWidget extends AppWidgetProvider {

    private static String ACTION_SYLLABUS_REFRESH = "ifafu.widget.syllabus.REFRESH";

    private void updateSyllabusWidget(Context context, AppWidgetManager appWidgetManager,
                                      int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.syllabus_widget);
        updateSyllabusWidget(context, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private PendingIntent getPendingIntent(Context context, int resId) {
        Intent intent = new Intent();
        intent.setClass(context, SyllabusWidget.class);
        intent.setAction(ACTION_SYLLABUS_REFRESH);
        intent.setData(Uri.parse("Id:" + resId));
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @SuppressLint("DefaultLocale")
    private void updateSyllabusWidget(Context context, RemoteViews remoteViews) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日 E", Locale.CHINA);
        Date date = new Date();
        remoteViews.setOnClickPendingIntent(R.id.tv_refresh, getPendingIntent(context, R.id.tv_refresh));
        remoteViews.setTextViewText(R.id.tv_refresh_time, context.getString(R.string.refresh_time_format, format.format(date)));

        SyllabusModel model = new SyllabusModel(context);
        NextCourse next = model.getNextCourse();
        switch (next.getResult()) {
            case NextCourse.IN_HOLIDAY:
                remoteViews.setTextViewText(R.id.tv_time, String.format("%s 放假中", format1.format(date)));
            case NextCourse.EMPTY_DATA:
            case NextCourse.NO_TODAY_COURSE:
            case NextCourse.NO_NEXT_COURSE:
                isShowCourseInfo(remoteViews, false);
                remoteViews.setTextViewText(R.id.tv_tip, next.getTitle());
                break;
            case NextCourse.HAS_NEXT_COURSE:
                isShowCourseInfo(remoteViews, true);
                remoteViews.setTextViewText(R.id.tv_course_name, next.getTitle() + next.getName());
                remoteViews.setTextViewText(R.id.tv_course_address, next.getAddress());
                remoteViews.setTextViewText(R.id.tv_course_node,
                        MessageFormat.format("{0} {1}", next.getNodeText(), next.getTimeText()));
                break;
        }
        remoteViews.setTextViewText(R.id.tv_time, String.format("%s %s", format1.format(date), next.getWeekText()));
    }

    private void isShowCourseInfo(RemoteViews remoteViews, boolean isShow) {
        if (isShow) {
            remoteViews.setViewVisibility(R.id.tv_course_name, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.tv_course_node, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.tv_course_address, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.tv_tip, View.GONE);
        } else {
            remoteViews.setViewVisibility(R.id.tv_course_name, View.GONE);
            remoteViews.setViewVisibility(R.id.tv_course_node, View.GONE);
            remoteViews.setViewVisibility(R.id.tv_course_address, View.GONE);
            remoteViews.setViewVisibility(R.id.tv_tip, View.VISIBLE);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateSyllabusWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(ACTION_SYLLABUS_REFRESH)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.syllabus_widget);
            Uri data = intent.getData();
            int resId = -1;
            if (data != null) {
                resId = Integer.parseInt(data.getSchemeSpecificPart());
            }
            switch (resId) {
                case R.id.tv_refresh:
                    updateSyllabusWidget(context, remoteViews);
                    break;
            }
            AppWidgetManager manger = AppWidgetManager.getInstance(context);
            ComponentName thisName = new ComponentName(context, SyllabusWidget.class);
            manger.updateAppWidget(thisName,remoteViews);
        } else {
            super.onReceive(context, intent);
        }
    }

}

