package cn.ifafu.ifafu.mvp.syllabus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SyllabusPresenter extends BaseZFPresenter<SyllabusContract.View, SyllabusContract.Model>
        implements SyllabusContract.Presenter {

    SyllabusPresenter(SyllabusContract.View view) {
        super(view, new SyllabusModel(view.getContext()));
    }

    @Override
    public void onStart() {
        mView.setSyllabusRowCount(mModel.getRowCount());
        mView.setCourseBeginTime(mModel.getCourseBeginTime());
        String firstStudyDay = mModel.getFirstStudyDay();
        mView.setFirstStudyDay(firstStudyDay);
        int firstDayOfWeek = mModel.getFirstDayOfWeek();
        mView.setFirstDayOfWeek(firstDayOfWeek);
        mView.setCurrentWeek(getCurrentWeek(firstStudyDay, firstDayOfWeek));
        updateSyllabus(false, false);
    }

    @Override
    public void updateSyllabus(boolean refresh, boolean showToast) {
        Observable<List<Course>> observable;
        if (refresh) {
            observable = onlineCoursesObservable();
        } else {
            observable = Observable
                    .<List<Course>>create(emitter -> {
                        List<Course> courseList2 = mModel.getAllCoursesFromDB();
                        emitter.onNext(courseList2);
                        emitter.onComplete();
                    })
                    .flatMap(o -> {
                        if (o.isEmpty()) {
                            return onlineCoursesObservable();
                        } else {
                            return Observable.just(o);
                        }
                    });
        }
        mCompDisposable.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    if (showToast) {
                        mView.showMessage(R.string.refresh_success);
                    }
                    mView.setSyllabusDate(list);
                    mView.redrawSyllabus();
                }, this::onError)
        );
    }

    private Observable<List<Course>> onlineCoursesObservable() {
        return mModel.getCoursesFromNet()
                .map(courses -> {
                    // 保存到数据库
                    mModel.clearOnlineCourses();
                    mModel.saveCourses(courses);
                    return courses;
                })
                .map(courses -> {
                    // 加入本地课程
                    courses.addAll(mModel.getLocalCoursesFromDB());
                    return courses;
                })
                .retryWhen(this::ensureTokenAlive);// 保活
    }

    private int getCurrentWeek(String firstStudyDay, int firstDayOfWeek) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date firstStudyDate = format.parse(firstStudyDay);
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(firstDayOfWeek);
            int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(firstStudyDate);
            int firstWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            return currentWeek - firstWeek + 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onDelete(Course course) {
        mModel.deleteCourse(course);
        updateSyllabus(false, false);
    }
}
