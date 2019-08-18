package cn.ifafu.ifafu.mvp.syllabus_item;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

class SyllabusItemPresenter extends BasePresenter<SyllabusItemContract.View, SyllabusItemContract.Model>
        implements SyllabusItemContract.Presenter {

    private int come_from;

    private int resultCode = Activity.RESULT_CANCELED;

    private Course course;

    SyllabusItemPresenter(SyllabusItemContract.View view) {
        super(view, new SyllabusItemModel(view.getContext()));
    }

    @Override
    public void onStart() {
        Intent intent = mView.getActivity().getIntent();
        // 1 表示通过添加按钮打开Activity
        come_from = intent.getIntExtra("come_from", -1);
        if (come_from == 1) {
            mView.isEditMode(true);
        }
        // 获取跳转课程id
        long id = intent.getLongExtra("course_id", -1);
        Log.d(TAG, "come_from: " + come_from + ", course_id: " + id);
        if (id != -1) {
            course = mModel.getCourseById(id);
            if (course != null) {
                resetView(course);
            }
        }
        if (course == null) {
            course = new Course();
        }
    }

    private void resetView(Course course) {
        mView.setNameText(course.getName());
        mView.setTeacherText(course.getTeacher());
        mView.setAddressText(course.getAddress());
        mView.setWeekData(course.getWeekSet());
        mView.setTimeOPVSelect(course.getWeekday() - 1, course.getBeginNode(),
                course.getBeginNode() + course.getNodeCnt() -1);
    }

    @Override
    public void onFinish() {
        mView.getActivity().setResult(resultCode);
        mView.killSelf();
    }

    @Override
    public void onSave() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    String name = mView.getNameText();
                    if (name.isEmpty()) {
                        return R.string.input_course_name;
                    }
                    if (course.getNodeCnt() == 0) {
                        return R.string.select_course_time;
                    }
                    if (mView.getWeekData().isEmpty()) {
                        return R.string.select_course_week;
                    }
                    String address = mView.getAddressText();
                    String teacher = mView.getTeacherText();
                    course.setName(name);
                    course.setTeacher(teacher);
                    course.setAddress(address);
                    mModel.save(course);
                    resultCode = Activity.RESULT_OK;
                    return R.string.save_successful;
                })
                .compose(RxUtils.ioToMain())
                .subscribe(stringRes -> {
                    mView.showMessage(stringRes);
                    if (come_from == 1) {
                        mView.getActivity().setResult(resultCode);
                        mView.killSelf();
                    } else {
                        resetView(course);
                        mView.isEditMode(false);
                    }
                }, this::onError)
        );
    }

    @Override
    public void onEdit() {
        mView.isEditMode(true);
    }

    @Override
    public void onDelete() {
        mModel.delete(course);
        mView.showMessage(R.string.delete_successful);
        mView.getActivity().setResult(Activity.RESULT_OK);
        mView.killSelf();
    }

    @Override
    public void onTimeSelect(int options1, int options2, int options3) {
        course.setWeekday(options1 + 1);
        course.setBeginNode(options2 + 1);
        course.setNodeCnt(options3 - options2);
    }
}
