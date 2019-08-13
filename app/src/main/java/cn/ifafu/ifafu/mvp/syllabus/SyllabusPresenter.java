package cn.ifafu.ifafu.mvp.syllabus;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.util.RxUtils;
import cn.ifafu.ifafu.view.syllabus.data.DayOfWeek;
import io.reactivex.Observable;

public class SyllabusPresenter extends BaseZFPresenter<SyllabusContract.View, SyllabusContract.Model>
        implements SyllabusContract.Presenter {

    SyllabusPresenter(SyllabusContract.View view) {
        mView = view;
        mModel = new SyllabusModel(view.getContext());
    }

    @Override
    public void onStart() {
        mView.setSyllabusRowCount(mModel.getRowCount());
        mView.setCourseBeginTime(mModel.getCourseBeginTime());
        String firstStudyDay = mModel.getFirstStudyDay();
        mView.setFirstStudyDay(firstStudyDay);
        // 设置每周首日
        int firstDayOfWeek = mModel.getFirstDayOfWeek();
        mView.setFirstDayOfWeek(firstDayOfWeek);
        // 设置当前周
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date firstStudyDate = format.parse(firstStudyDay);
            mView.setCurrentWeek(getCurrentWeek(firstStudyDate, firstDayOfWeek));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updateSyllabus(false, false);
    }

    @Override
    public void updateSyllabus(boolean refresh, boolean showToast) {
        mCompDisposable.add(Observable.just(refresh)
                .flatMap(isRefresh -> {
                    if (isRefresh) {
                        Log.d(TAG, "force refresh");
                        return onlineCoursesObservable()
                                .doOnNext(courses -> {
                                    // 保存到数据库
                                    mModel.clearOnlineCourses();
                                    mModel.saveCourses(courses);
                                })
                                .map(courses -> {
                                    // 加入本地课程
                                    courses.addAll(mModel.getLocalCoursesFromDB());
                                    return courses;
                                });
                    } else {
                        Log.d(TAG, "not force refresh");
                        return Observable.fromCallable(() -> mModel.getAllCoursesFromDB())
                                .flatMap(o -> {
                                    if (o.isEmpty()) {
                                        return mModel.getCoursesFromNet()
                                                .retryWhen(this::ensureTokenAlive)
                                                .doOnNext(courses -> mModel.saveCourses(courses));
                                    } else {
                                        return Observable.just(o);
                                    }
                                });
                    }
                })
                .compose(RxUtils.ioToMainScheduler())
                .doOnSubscribe( disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .doOnNext(courses -> {
                    if (showToast) {
                        mView.showMessage(R.string.refresh_success);
                    }
                })
                .subscribe(list -> {
                    mView.setSyllabusDate(list);
                    mView.redrawSyllabus();
                }, this::onError)
        );
    }

    private Observable<List<Course>> onlineCoursesObservable() {
        return mModel.getCoursesFromNet()
                .retryWhen(this::ensureTokenAlive); // 保活
    }

    private int getCurrentWeek(Date firstStudyDate, @DayOfWeek int firstDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        int currentYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(firstStudyDate);
        int firstYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return currentYearWeek - firstYearWeek + 1;
    }

    @Override
    public void onDelete(Course course) {
        mModel.deleteCourse(course);
        updateSyllabus(false, false);
    }
}
