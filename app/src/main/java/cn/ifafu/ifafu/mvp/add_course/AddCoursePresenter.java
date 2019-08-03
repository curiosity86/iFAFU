package cn.ifafu.ifafu.mvp.add_course;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Course;
import cn.woolsen.android.mvp.BasePresenter;
import cn.woolsen.android.uitl.RxJavaUtils;

class AddCoursePresenter extends BasePresenter<AddCourseContract.View, AddCourseContract.Model>
        implements AddCourseContract.Presenter {

    private List<String> weekdays = Arrays.asList("周日", "周一", "周二", "周三", "周四", "周五", "周六");
    private List<String> nodes = new ArrayList<>();

    private List<String> weekTypes = Arrays.asList("全周", "单周", "双周");
    private List<String> weeks = new ArrayList<>();

    @SuppressLint("UseSparseArrays")
    private Map<Integer, Course> courses = new HashMap<>();

    AddCoursePresenter(AddCourseContract.View view) {
        super(view, new AddCourseModel(view.getContext()));
        for (int i = 1; i <= 13; i++) {
            nodes.add("第" + i + "节");
        }
        mView.setTimeOPVOptions(weekdays, nodes, nodes);
        for (int i = 1; i <= 24; i++) {
            weeks.add("第" + i + "周");
        }
        mView.setWeekOPVOptions(weekTypes, weeks, weeks);
    }

    @Override
    public String onWeekSelect(int op1, int op2, int op3, int layoutId) {
        Log.d(TAG, "onWeekSelect  => " + op1 + ", " + op2 + ", " + op3 + ", " + layoutId);
        Course course = courses.get(layoutId);
        if (course == null) course = new Course();
        course.setWeekType(op1);
        course.setBeginWeek(op2 + 1);
        course.setEndWeek(op3 + 1);
        courses.put(layoutId, course);
        return String.format("%s %s - %s", weekTypes.get(op1), weeks.get(op2), weeks.get(op3));
    }

    @Override
    public String onTimeSelect(int op1, int op2, int op3, int layoutId) {
        Log.d(TAG, "onTimeSelect  => " + op1 + ", " + op2 + ", " + op3 + ", " + layoutId);
        Course course = courses.get(layoutId);
        if (course == null) course = new Course();
        course.setWeekday(op1 + 1);
        course.setBeginNode(op2 + 1);
        course.setNodeCnt(op3 + 1 - op2);
        courses.put(layoutId, course);
        return String.format("%s %s - %s", weekdays.get(op1), nodes.get(op2), nodes.get(op3));
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
        mCompDisposable.add(RxJavaUtils
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
                        emitter.onComplete();
                    }
                })
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(
                        s -> {
                            mView.showMessage(s);
                            if (s.contains("保存")) {
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
