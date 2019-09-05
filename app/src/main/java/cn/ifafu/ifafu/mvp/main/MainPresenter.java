package cn.ifafu.ifafu.mvp.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.webkit.CookieManager;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.ifafu.ifafu.BuildConfig;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Holiday;
import cn.ifafu.ifafu.data.entity.NextCourse;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.exam.ExamModel;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.ifafu.ifafu.util.RxUtils;
import cn.ifafu.ifafu.view.timeline.TimeAxis;
import io.reactivex.Observable;

public class MainPresenter extends BaseZFPresenter<MainContract.View, MainContract.Model>
        implements MainContract.Presenter {

    MainPresenter(MainContract.View view) {
        super(view, new MainModel(view.getContext()));
    }

    @Override
    public void onStart() {
        mView.setLeftMenuHeadName(mModel.getUserName());
        mView.setLeftMenuHeadIcon(mModel.getSchoolIcon());
        // 获取主页菜单
        mCompDisposable.add(mModel.getMenus()
                .compose(RxUtils.ioToMain())
                .subscribe(menus -> mView.setMenuAdapterData(menus), this::onError)
        );
    }

    @Override
    public void updateApp() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    UpgradeInfo info = Beta.getUpgradeInfo();
                    return info != null && info.versionCode > GlobalLib.getLocalVersionCode(mView.getActivity());
                })
                .compose(RxUtils.ioToMain())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        Beta.checkUpgrade();
                    } else {
                        mView.showMessage(R.string.is_last_version);
                    }
                }, this::onError)
        );
    }

    @Override
    public void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "iFAFU下载链接：http://ifafu.cn");
        mView.openActivity(Intent.createChooser(intent, "分享"));
    }

    @Override
    public void updateView() {
        if (mView.getActivity().getIntent().getIntExtra("come_from", -1) != 0) {
            loginD = reLogin()
                    .compose(RxUtils.ioToMain())
                    .subscribe(response -> {
                    }, this::onError);
            mCompDisposable.add(loginD);
        }
        // 获取天气
        mCompDisposable.add(mModel.getWeather("101230101")
                .compose(RxUtils.ioToMain())
                .subscribe(weather -> mView.setWeatherText(weather), this::onError)
        );
        updateTimeLine();
        updateCourseView();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void updateCourseView() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    SyllabusModel model = new SyllabusModel(mView.getContext());
                    NextCourse next = model.getNextCourse();
//                    SyllabusSetting setting = model.getSyllabusSetting();
//                    Map<String, String> map = new HashMap<>();
//
//                    int currentWeek = model.getCurrentWeek();
//                    if (currentWeek == -1) {
//                        map.put("title", "放假中");
//                        return map;
//                    }
//
//                    List<Course> courses = model.getAllCoursesFromDB();
//                    if (courses.isEmpty()) {
//                        map.put("title", "暂无课程信息");
//                        return map;
//                    }
//
//                    int currentWeekday = DateUtils.getCurrentDayOfWeek();
//                    List<Course> todayCourses = model.getCoursesFromDB(currentWeek, currentWeekday);
//                    Collections.sort(todayCourses, (o1, o2) -> Integer.compare(o1.getBeginNode(), o2.getBeginNode()));
//                    if (todayCourses.isEmpty()) {
//                        map.put("title", "今天没课呀！！");
//                        return map;
//                    }
//
//                    //计算下一节是第几节课
//                    int[] intTime = setting.getBeginTime();
//                    Calendar c = Calendar.getInstance();
//                    int now = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
//                    int nextNode = 9999;
//                    for (int i = 0; i < intTime.length; i++) {
//                        if (now < intTime[i]) {
//                            nextNode = i;
//                            break;
//                        }
//                    }
//                    Log.d(TAG, "updateCourseView => nextNode: " + nextNode + "    now: " + now);
//                    StringBuilder sb = new StringBuilder();
//                    for (Course co : todayCourses) {
//                        sb.append(co.getName()).append(", ");
//                    }
//                    Log.d(TAG, "course: " + "[" + sb + "]");
//
//                    Course nextCourse = null;
//                    for (Course course: todayCourses) {
//                        if (course.getBeginNode() > nextNode) {
//                            nextCourse = course;
//                            break;
//                        }
//                    }
//                    if (nextCourse != null) {
//                        map.put("title", "下一节课：");
//                        map.put("name", nextCourse.getName());
//                        map.put("address", nextCourse.getAddress());
//                        int length = setting.getNodeLength();
//                        int intStartTime = intTime[nextCourse.getBeginNode() - 1];
//                        int intEndTime = intTime[nextCourse.getBeginNode() + nextCourse.getNodeCnt() - 2];
//                        if (intEndTime % 100 + length >= 60) {
//                            intEndTime = intEndTime + 100 - (intEndTime % 100) + ((intEndTime % 100 + length) % 60);
//                        }
//                        map.put("time", String.format("%d:%02d-%d:%02d",
//                                intStartTime / 100,
//                                intStartTime % 100,
//                                intEndTime / 100,
//                                intEndTime % 100));
//                    } else {
//                        map.put("title", "今天课上完啦！！");
//                    }
                    return next;
                })
                .compose(RxUtils.computationToMain())
                .subscribe(next -> {
                    switch (next.getResult()) {
                        case NextCourse.IN_HOLIDAY:
                        case NextCourse.EMPTY_DATA:
                        case NextCourse.NO_TODAY_COURSE:
                        case NextCourse.NO_NEXT_COURSE:
                            mView.setCourseText(next.getTitle(), "", "", "");
                            break;
                        case NextCourse.HAS_NEXT_COURSE:
                            mView.setCourseText(next.getTitle(),  next.getName(), next.getAddress(), next.getTimeText());
                            break;
                    }
                }, throwable -> {
                    onError(throwable);
                    mView.setCourseText("获取课程信息失败", "", "", "");
                })
        );
    }

    private void updateTimeLine() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    List<TimeAxis> list = new ArrayList<>();
                    long now = System.currentTimeMillis();

                    List<Holiday> holidays = mModel.getHoliday();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    for (Holiday holiday : holidays) {
                        Date date = format.parse(holiday.getDate());
                        long interval = date.getTime() - now;
                        if (interval > 0) {
                            int day = (int) (interval / 1000 / 60 / 60 / 24);
                            if (day != 0) {
                                TimeAxis axis = new TimeAxis(
                                        holiday.getName(), holiday.getDate(), day);
                                list.add(axis);
                            }
                        }
                    }

                    List<Exam> exams = new ExamModel(mView.getContext()).getThisTermExams();
                    for (Exam exam : exams) {
                        long interval = exam.getStartTime() - now;
                        if (interval > 0) {
                            int day = (int) (interval / 1000 / 60 / 60 / 24);
                            if (day != 0) {
                                TimeAxis axis = new TimeAxis(
                                        exam.getName(), format.format(new Date(exam.getStartTime())), day);
                                list.add(axis);
                            }
                        }
                    }
                    Collections.sort(list, (o1, o2) -> Integer.compare(o1.getDay(), o2.getDay()));
                    return list.subList(0, list.size() < 4 ? list.size() : 4);
                })
                .compose(RxUtils.ioToMain())
                .subscribe(list -> mView.setTimeLineData(list), this::onError)
        );
    }

    @Override
    public void quitAccount() {
        if (BuildConfig.DEBUG) {
            mModel.clearAllDate();
        }
        CookieManager.getInstance().removeAllCookies(null);
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        mView.openActivity(intent);
        mView.killSelf();
    }
}
