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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel;
import cn.ifafu.ifafu.util.DateUtils;

/**
 * Implementation of App Widget functionality.
 */
public class SyllabusWidget extends AppWidgetProvider {

    private static String ACTION_SYLLABUS_REFRESH = "ifafu.widget.syllabus.REFRESH";

    private static List<Course> courses;

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
        remoteViews.setOnClickPendingIntent(R.id.tv_refresh, getPendingIntent(context, R.id.tv_refresh));
        remoteViews.setTextViewText(R.id.tv_refresh_time, context.getString(R.string.refresh_time_format, format.format(new Date())));

        SyllabusModel model = new SyllabusModel(context);
        SyllabusSetting setting = model.getSyllabusSetting();

        int currentWeek = model.getCurrentWeek();
        if (currentWeek == -1) {
            isShowCourseInfo(remoteViews, false);
            remoteViews.setTextViewText(R.id.tv_tip, "放假中");
            return;
        }

        List<Course> courses = model.getAllCoursesFromDB();
        if (courses.isEmpty()) {
            isShowCourseInfo(remoteViews, false);
            remoteViews.setTextViewText(R.id.tv_tip, "暂无课程信息");
            return;
        }

        int currentWeekday = DateUtils.getCurrentDayOfWeek();
        List<Course> todayCourses = model.getCoursesFromDB(currentWeek, currentWeekday);
        Collections.sort(todayCourses, (o1, o2) -> Integer.compare(o1.getBeginNode(), o2.getBeginNode()));
        if (todayCourses.isEmpty()) {
            isShowCourseInfo(remoteViews, false);
            remoteViews.setTextViewText(R.id.tv_tip, "今天没课呀！！");
            return;
        }

        //计算下一节是第几节课
        int[] intTime = setting.getBeginTime();
        Calendar c = Calendar.getInstance();
        int now = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        int nextNode = 9999;
        for (int i = 0; i < intTime.length; i++) {
            if (now < intTime[i]) {
                nextNode = i;
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Course co : todayCourses) {
            sb.append(co.getName()).append(", ");
        }

        Course nextCourse = null;
        for (Course course: todayCourses) {
            if (course.getBeginNode() > nextNode) {
                nextCourse = course;
                break;
            }
        }
        if (nextCourse != null) {
            isShowCourseInfo(remoteViews, true);
            remoteViews.setTextViewText(R.id.tv_course_name,
                    context.getString(R.string.next_course_format, nextCourse.getName()));
            remoteViews.setTextViewText(R.id.tv_course_address, nextCourse.getAddress());
            int length = setting.getNodeLength();
            int intStartTime = intTime[nextCourse.getBeginNode() - 1];
            int intEndTime = intTime[nextCourse.getBeginNode() + nextCourse.getNodeCnt() - 2];
            if (intEndTime % 100 + length >= 60) {
                intEndTime = intEndTime + 100 - (intEndTime % 100) + ((intEndTime % 100 + length) % 60);
            }
            String time =  String.format("%d:%02d-%d:%02d",
                    intStartTime / 100,
                    intStartTime % 100,
                    intEndTime / 100,
                    intEndTime % 100);
            remoteViews.setTextViewText(R.id.tv_course_node, context.getString(R.string.next_node_format,
                            nextCourse.getBeginNode(), nextCourse.getEndNode(), time));
        } else {
            isShowCourseInfo(remoteViews, false);
            remoteViews.setTextViewText(R.id.tv_tip, "今天课上完啦！！");
        }
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

