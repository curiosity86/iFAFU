package cn.ifafu.ifafu.mvp.syllabus;

import java.util.Calendar;
import java.util.Date;

import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.woolsen.android.uitl.DateUtils;
import cn.woolsen.android.uitl.RxJavaUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SyllabusPresenter extends BaseZFPresenter<SyllabusContract.View, SyllabusContract.Model>
        implements SyllabusContract.Presenter {

    private final int firstDayOfWeek = Calendar.SUNDAY;

    SyllabusPresenter(SyllabusContract.View view) {
        super(view, new SyllabusModel(view.getContext()));
    }

    @Override
    public void onStart() {
        mView.setSyllabusRowCount(mModel.getRowCount());
        mView.setCourseBeginTime(mModel.getCourseBeginTime());
        mView.setFirstDayOfWeek(firstDayOfWeek);
        updateSyllabus(false, false);
    }

    @Override
    public void updateSyllabus() {
        updateSyllabus(true, true);
    }

    /**
     * 若数据库为空，则自动刷新
     *
     * @param refresh   是否强制刷新
     * @param showToast 加载完是否显示Toast
     */
    private void updateSyllabus(boolean refresh, boolean showToast) {
        mCompDisposable.add(mModel
                .getAllCoursesFromDB()
                .map(courses -> {
                    if (refresh) {
                        mModel.clearOnlineCourses();
                        courses.removeIf(course -> !course.getLocal());
                    }
                    return courses;
                })
                .flatMap(courses -> {
                    // 如果数据库为空，则从网络上拉取课表并保存
                    if (courses.isEmpty() || refresh) {
                        return mModel.getCoursesFromNet()
                                .retryWhen(this::ensureTokenAlive)
                                .doOnNext(courses1 -> mModel.saveCourses(courses1));
                    } else {
                        return Observable.just(courses);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(courses -> {
                    if (showToast) {
                        mView.showMessage("刷新成功");
                    }
                    mView.setSyllabusDate(courses);
                    mView.redrawSyllabus();
                }, this::onError));
    }

}
