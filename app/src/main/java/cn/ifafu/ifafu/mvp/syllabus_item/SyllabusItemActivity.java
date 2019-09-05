package cn.ifafu.ifafu.mvp.syllabus_item;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.adapter.WeekItemAdapter;
import cn.ifafu.ifafu.view.custom.WToolbar;

public class SyllabusItemActivity extends BaseActivity<SyllabusItemContract.Presenter>
        implements SyllabusItemContract.View {

    @BindView(R.id.btn_edit)
    ImageButton btnEdit;
    @BindView(R.id.btn_delete)
    ImageButton btnDelete;
    @BindView(R.id.btn_ok)
    ImageButton btnOk;
    @BindView(R.id.tb_exam_item)
    WToolbar tbExamItem;
    @BindView(R.id.rv_course_weeks)
    RecyclerView rvCourseWeeks;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.et_course_name)
    EditText etCourseName;
    @BindView(R.id.tv_course_time)
    TextView tvCourseTime;
    @BindView(R.id.et_course_time)
    TextView etCourseTime;
    @BindView(R.id.tv_course_address)
    TextView tvCourseAddress;
    @BindView(R.id.et_course_address)
    EditText etCourseAddress;
    @BindView(R.id.tv_course_teacher)
    TextView tvCourseTeacher;
    @BindView(R.id.et_course_teacher)
    EditText etCourseTeacher;

    private OptionsPickerView<String> timeOPV;

    private WeekItemAdapter weekAdapter;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_syllabus_item;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tbExamItem)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init();

        mPresenter = new SyllabusItemPresenter(this);

        weekAdapter = new WeekItemAdapter(this);
        rvCourseWeeks.setLayoutManager(new GridLayoutManager(this, 6, RecyclerView.VERTICAL, false));
        rvCourseWeeks.setAdapter(weekAdapter);

        tbExamItem.setNavigationOnClickListener(v -> finish());

        timeOPV = new OptionsPickerBuilder(this,
                (options1, options2, options3, v) -> {
                    if (options2 > options3) {
                        options3 = options2;
                    }
                    timeOPV.setSelectOptions(options1, options2, options3);
                    mPresenter.onTimeSelect(options1, options2, options3);
                })
                .setOptionsSelectChangeListener((options1, options2, options3) -> {
                    if (options2 > options3) {
                        timeOPV.setSelectOptions(options1, options2, options2);
                    }
                })
                .setOutSideCancelable(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择时间")
                .setTitleColor(Color.parseColor("#157efb"))
                .setTitleSize(13)
                .build();

    }

    private void setTimeText(String text) {
        tvCourseTime.setText(text);
        etCourseTime.setText(text);
    }

    @OnClick({R.id.btn_edit, R.id.btn_delete, R.id.btn_ok, R.id.et_course_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_course_time:
                timeOPV.show();
                break;
            case R.id.btn_edit:
                mPresenter.onEdit();
                break;
            case R.id.btn_delete:
                mPresenter.onDelete();
                break;
            case R.id.btn_ok:
                mPresenter.onSave();
                break;
        }
    }

    @Override
    public void setTimeOPVOptions(List<String> op1, List<String> op2, List<String> op3) {
        timeOPV.setNPicker(op1, op2, op3);
    }

    @Override
    public TreeSet<Integer> getWeekData() {
        return weekAdapter.getWeekList();
    }

    @Override
    public void setWeekData(TreeSet<Integer> weekData) {
        weekAdapter.setWeekList(weekData);
        weekAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTimeOPVSelect(int op1, int op2, int op3, String text) {
        timeOPV.setSelectOptions(op1, op2, op3);
        setTimeText(text);
    }

    @Override
    public void isEditMode(boolean isEditMode) {
        int v1, v2;
        weekAdapter.EDIT_MODE = isEditMode;
        if (isEditMode) {
            v1 = View.VISIBLE;
            v2 = View.GONE;
        } else {
            v1 = View.GONE;
            v2 = View.VISIBLE;
        }
        btnOk.setVisibility(v1);
        etCourseTime.setVisibility(v1);
        etCourseName.setVisibility(v1);
        etCourseAddress.setVisibility(v1);
        etCourseTeacher.setVisibility(v1);
        btnDelete.setVisibility(v2);
        btnEdit.setVisibility(v2);
        tvCourseAddress.setVisibility(v2);
        tvCourseName.setVisibility(v2);
        tvCourseTeacher.setVisibility(v2);
        tvCourseTime.setVisibility(v2);
    }

    @Override
    public String getNameText() {
        return etCourseName.getText().toString();
    }

    @Override
    public String getAddressText() {
        return etCourseAddress.getText().toString();
    }

    @Override
    public String getTeacherText() {
        return etCourseTeacher.getText().toString();
    }

    @Override
    public void setNameText(String name) {
        tvCourseName.setText(name);
        etCourseName.setText(name);
    }

    @Override
    public void setAddressText(String address) {
        tvCourseAddress.setText(address);
        etCourseAddress.setText(address);
    }

    @Override
    public void setTeacherText(String teacher) {
        tvCourseTeacher.setText(teacher);
        etCourseTeacher.setText(teacher);
    }
}
