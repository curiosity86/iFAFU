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

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Holiday;
import cn.ifafu.ifafu.data.entity.NextCourse;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.exam.ExamModel;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel;
import cn.ifafu.ifafu.util.DateUtils;
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
    public void onCreate() {
        mView.setLeftMenuHeadName(mModel.getUserName());
        mView.setLeftMenuHeadIcon(mModel.getSchoolIcon());
        // 获取主页菜单
        mCompDisposable.add(mModel.getMenus()
                .compose(RxUtils.ioToMain())
                .subscribe(menus -> mView.setMenuAdapterData(menus), this::onError)
        );
        updateWeather();
        updateTimeLine();
        updateNextCourseView();
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
    public void onRefresh() {
        if (mView.getActivity().getIntent().getIntExtra("come_from", -1) != 0) {
            IFAFU.loginDisposable = mModel.reLogin()
                    .compose(RxUtils.ioToMain())
                    .subscribe(response -> {
                    }, this::onError);
        }
        updateWeather();
        updateTimeLine();
        updateNextCourseView();
    }

    @Override
    public void updateWeather() {
        // 获取天气
        mCompDisposable.add(mModel.getWeather("101230101")
                .compose(RxUtils.ioToMain())
                .subscribe(weather -> mView.setWeatherText(weather), this::onError)
        );
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void updateNextCourseView() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    SyllabusModel model = new SyllabusModel(mView.getContext());
                    return model.getNextCourse();
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
                            mView.setCourseText(next.getTitle(), next.getName(), next.getAddress(), next.getTimeText());
                            break;
                    }
                }, throwable -> {
                    onError(throwable);
                    mView.setCourseText("获取课程信息失败", "", "", "");
                })
        );
    }

    @Override
    public void updateTimeLine() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    List<TimeAxis> list = new ArrayList<>();
                    Date now = new Date();

                    List<Holiday> holidays = mModel.getHoliday();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    for (Holiday holiday : holidays) {
                        Date date = format.parse(holiday.getDate());
                        int day = DateUtils.calcLastDays(now, date);
                        if (day >= 0) {
                            TimeAxis axis = new TimeAxis(
                                    holiday.getName(), holiday.getDate(), day);
                            list.add(axis);
                        }
                    }

                    List<Exam> exams = new ExamModel(mView.getContext()).getThisTermExams();
                    for (Exam exam : exams) {
                        Date date = new Date(exam.getStartTime());
                        int day = DateUtils.calcLastDays(now, date);
                        if (day >= 0) {
                            TimeAxis axis = new TimeAxis(
                                    exam.getName(), format.format(new Date(exam.getStartTime())), day);
                            list.add(axis);
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
        CookieManager.getInstance().removeAllCookies(null);
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        mModel.clearAllDate();
        mView.openActivity(intent);
        mView.killSelf();
    }
}
