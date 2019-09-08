package cn.ifafu.ifafu.mvp.syllabus;

import android.annotation.SuppressLint;

import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

public class SyllabusPresenter extends BaseZFPresenter<SyllabusContract.View, SyllabusContract.Model>
        implements SyllabusContract.Presenter {

    SyllabusPresenter(SyllabusContract.View view) {
        super(view, new SyllabusModel(view.getContext()));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        mView.setSyllabusSetting(mModel.getSyllabusSetting());
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
    public void updateSyllabusSetting() {
        mView.setSyllabusSetting(mModel.getSyllabusSetting());
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
                .doOnNext(courses -> {
                    // 保存到数据库
                    mModel.clearOnlineCourses();
                    mModel.saveCourses(courses);
                }); // 保活
    }

    @Override
    public void onDelete(Course course) {
        mModel.deleteCourse(course);
        updateSyllabusLocal();
    }
}
