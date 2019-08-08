package cn.ifafu.ifafu.mvp.add_course;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

class AddCoursePresenter extends BasePresenter<AddCourseContract.View, AddCourseContract.Model>
        implements AddCourseContract.Presenter {

    private Course editCourse;

    @SuppressLint("UseSparseArrays")
    private Map<Integer, Course> courses = new HashMap<>();

    AddCoursePresenter(AddCourseContract.View view) {
        super(view, new AddCourseModel(view.getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        long id = mView.getActivity().getIntent().getLongExtra("id", -1);
        if (id != -1) {
            mView.editMode();
            onAdd();
            editCourse = mModel.getCourseById(id);
            mView.setNameText(editCourse.getName());
            mView.setTeacherText(editCourse.getTeacher());
            mView.setAddressText(editCourse.getAddress());
            mView.setTimeOPVSelect(editCourse.getWeekday() - 1, editCourse.getBeginNode() -1,
                    editCourse.getBeginNode() + editCourse.getNodeCnt() - 2);
            if (editCourse.getWeekType() == Course.ALL_WEEK) {
                mView.setWeekOPVSelect(0, editCourse.getBeginWeek() - 1, editCourse.getEndWeek() - 1);
            } else if (editCourse.getWeekType() == Course.SINGLE_WEEK) {
                mView.setWeekOPVSelect(1, editCourse.getBeginWeek() - 1, editCourse.getEndWeek() - 1);
            } else if (editCourse.getWeekType() == Course.DOUBLE_WEEK) {
                mView.setWeekOPVSelect(2, editCourse.getBeginWeek() - 1, editCourse.getEndWeek() - 1);
            }
        } else {
            onAdd();
        }
    }

    @Override
    public void onWeekSelect(int op1, int op2, int op3, int layoutId) {
        Log.d(TAG, "onWeekSelect  => " + op1 + ", " + op2 + ", " + op3 + ", " + layoutId);
        Course course = courses.get(layoutId);
        if (course == null) course = new Course();
        if (op1 == 0) {
            course.setWeekType(Course.ALL_WEEK);
        } else if (op2 == 1) {
            course.setWeekType(Course.SINGLE_WEEK);
        } else if (op3 == 2){
            course.setWeekType(Course.DOUBLE_WEEK);
        }
        course.setBeginWeek(op2 + 1);
        course.setEndWeek(op3 + 1);
        courses.put(layoutId, course);
    }

    @Override
    public void onTimeSelect(int op1, int op2, int op3, int layoutId) {
        Course course = courses.get(layoutId);
        if (course == null) course = new Course();
        course.setWeekday(op1 + 1);
        course.setBeginNode(op2 + 1);
        course.setNodeCnt(op3 + 1 - op2);
        courses.put(layoutId, course);
    }

    @Override
    public void onDelete(int layoutId) {
        Log.d(TAG, "onDelete  => " + layoutId);
        courses.remove(layoutId);
    }

    @Override
    public void onSave() {

        Log.d(TAG, "onSave => Size -> " + courses.size());
        String name = mView.getNameText();
        String teacher = mView.getTeacherText();
        String account = mModel.getAccount();
        mCompDisposable.add(Observable
                .<String>create(emitter -> {
                    if (name.isEmpty()) {
                        emitter.onNext("请输入课程名称");
                        emitter.onComplete();
                    } else if (courses.isEmpty()) {
                        emitter.onNext("请添加时间段");
                        emitter.onComplete();
                    } else if (account.isEmpty()) {
                        emitter.onError(new Resources.NotFoundException("Account not found!!"));
                    } else {
                        if (editCourse == null) {
                            for (Map.Entry<Integer, Course> e : courses.entrySet()) {
                                Course c = e.getValue();
                                if (c.getBeginWeek() == -1) {
                                    emitter.onNext("请选择上课周数");
                                    emitter.onComplete();
                                    return;
                                } else if (c.getBeginNode() == -1) {
                                    emitter.onNext("请选择上课时间");
                                    emitter.onComplete();
                                    return;
                                }
                                c.setName(name);
                                c.setTeacher(teacher);
                                c.setAddress(mView.getAddressText(e.getKey()));
                                c.setLocal(true);
                                c.setAccount(account);
                                c.setId((long) c.hashCode());
                                Log.d(TAG, "onSave => Course -> " + c.toString());
                            }
                            mModel.save(courses.values());
                            emitter.onNext("保存" + courses.size() + "节本地课程");
                        } else {
                            mModel.save(editCourse);
                            editCourse.setName(name);
                            editCourse.setTeacher(teacher);
                            editCourse.setAddress(mView.getAddressText(0));
                            emitter.onNext("编辑 " + editCourse.getName() + " 成功");
                        }
                    }
                    emitter.onComplete();
                })
                .compose(RxUtils.ioToMainScheduler())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(
                        s -> {
                            mView.showMessage(s);
                            if (s.contains("保存") || s.contains("成功")) {
                                mView.getActivity().setResult(Activity.RESULT_OK);
                                mView.killSelf();
                            }
                        }
                        , this::onError
                )
        );
    }

    @Override
    public void onAdd() {
        int layoutId = courses.size();
        Log.d(TAG, "onAdd  => " + layoutId);
        courses.put(layoutId, new Course());
        mView.addTimeView(layoutId);
    }
}
