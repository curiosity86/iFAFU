package cn.ifafu.ifafu.mvp.syllabus;

import android.annotation.SuppressLint;

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
        super(view, new SyllabusModel(view.getContext()));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onStart() {
        int count = mModel.getRowCount();
        mView.setSyllabusRowCount(mModel.getRowCount());
        String[] strTime = new String[count];
        int[] intTime = mModel.getCourseBeginTime();
        for (int i = 0; i < strTime.length && i < intTime.length; i++) {
            strTime[i] = String.format("%d:%02d", intTime[i] / 100, intTime[i] % 100);
        }
        mView.setCourseBeginTime(strTime);
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
        updateSyllabusLocal();
    }

    @Override
    public void updateSyllabusNet() {
        mCompDisposable.add(onlineCoursesObservable()
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setSyllabusDate(list);
                    mView.redrawSyllabus();
                    mView.showMessage(R.string.syllabus_refresh_success);
                }, this::onError)
        );
    }

    @Override
    public void updateSyllabusLocal() {
        mCompDisposable.add(Observable
                .fromCallable(() -> mModel.getAllCoursesFromDB())
                .flatMap(o -> {
                    if (o.isEmpty()) {
                        return onlineCoursesObservable();
                    } else {
                        return Observable.just(o);
                    }
                })
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setSyllabusDate(list);
                    mView.redrawSyllabus();
                }, this::onError)
        );
    }

    private Observable<List<Course>> onlineCoursesObservable() {
        return mModel.getCoursesFromNet()
                .retryWhen(this::ensureTokenAlive)
                .doOnNext(courses -> {
                    // 保存到数据库
                    mModel.clearOnlineCourses();
                    mModel.saveCourses(courses);
                }); // 保活
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
        updateSyllabusLocal();
    }
}
