package cn.ifafu.ifafu.mvp.add_course;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.jaeger.library.StatusBarUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.woolsen.android.mvp.BaseActivity;
import cn.woolsen.android.uitl.ColorUtils;

public class AddCourseActivity extends BaseActivity<AddCourseContract.Presenter>
        implements AddCourseContract.View, View.OnClickListener {

    private OptionsPickerView<String> weekOPV;
    private OptionsPickerView<String> timeOPV;

    private Button addBtn;

    private TextView nameTV;
    private TextView teacherTV;
    private LinearLayout timeFragment;

    private long lastBack = 0;

    private final List<String> weekTypes = Arrays.asList("全周", "单周", "双周");
    private final List<String> weekdays = Arrays.asList("周日", "周一", "周二", "周三", "周四", "周五", "周六");
    private List<String> nodes = new ArrayList<>();
    private List<String> weeks = new ArrayList<>();

    private View firstView;

    // 编辑课程模式
    private boolean editMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        StatusBarUtil.setColor(this, ColorUtils.getColor(getContext(), R.color.global_blue), 0);

        mPresenter = new AddCoursePresenter(this);

        nameTV = findViewById(R.id.et_name);
        teacherTV = findViewById(R.id.et_teacher);
        timeFragment = findViewById(R.id.fragment_time);

        addBtn = findViewById(R.id.btn_add);
        addBtn.setOnClickListener(this);

        findViewById(R.id.btn_finish).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        initOptionPickerView();

        mPresenter.onStart();
    }

    private void initOptionPickerView() {
        weekOPV = new OptionsPickerBuilder(this,
                (options1, options2, options3, v) -> {
                    if (options2 > options3) {
                        options3 = options2;
                    }
                    weekOPV.setSelectOptions(options1, options2, options3);
                    setWeekText((TextView) v, options1, options2, options3);
                })
                .setOptionsSelectChangeListener((options1, options2, options3) -> {
                    if (options2 > options3) {
                        options3 = options2;
                    }
                    weekOPV.setSelectOptions(options1, options2, options3);
                })
                .setOutSideCancelable(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择起止周")
                .setTitleSize(13)
                .setTitleColor(Color.parseColor("#157efb"))
                .build();

        timeOPV = new OptionsPickerBuilder(this,
                (options1, options2, options3, v) -> {
                    if (options2 > options3) {
                        options3 = options2;
                    }
                    timeOPV.setSelectOptions(options1, options2, options3);
                    mPresenter.onTimeSelect(options1, options2, options3, (((View) v.getParent().getParent()).getId()));
                    setTimeText((TextView) v, options1, options2, options3);
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

        for (int i = 1; i <= 12; i++) {
            nodes.add("第" + i + "节");
        }
        setTimeOPVOptions(weekdays, nodes, nodes);
        for (int i = 1; i <= 24; i++) {
            weeks.add("第" + i + "周");
        }
        setWeekOPVOptions(weekTypes, weeks, weeks);
    }

    private void setWeekText(TextView v, int op1, int op2, int op3) {
        v.setText(String.format("%s %s - %s", weekTypes.get(op1), weeks.get(op2), weeks.get(op3)));
    }

    private void setTimeText(TextView v, int op1, int op2, int op3) {
        v.setText(String.format("%s %s - %s", weekdays.get(op1), nodes.get(op2), nodes.get(op3)));
    }

    @Override
    public void setTimeOPVOptions(List<String> op1, List<String> op2, List<String> op3) {
        timeOPV.setNPicker(op1, op2, op3);
    }

    @Override
    public void setWeekOPVOptions(List<String> op1, List<String> op2, List<String> op3) {
        weekOPV.setNPicker(op1, op2, op3);
    }

    @Override
    public void setNameText(String name) {
        nameTV.setText(name);
    }

    @Override
    public void setTeacherText(String teacher) {
        teacherTV.setText(teacher);
    }

    @Override
    public String getNameText() {
        return nameTV.getText().toString();
    }

    @Override
    public String getTeacherText() {
        return teacherTV.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                finish();
                break;
            case R.id.btn_add:
                mPresenter.onAdd();
                break;
            case R.id.btn_delete:
                View root = (View) v.getParent().getParent();
                mPresenter.onDelete(root.getId());
                timeFragment.removeView(root);
                break;
            case R.id.btn_save:
                mPresenter.onSave();
                break;
            case R.id.tv_week:
                GlobalLib.hideSoftKeyboard(this);
                weekOPV.show(v);
                break;
            case R.id.tv_time:
                GlobalLib.hideSoftKeyboard(this);
                timeOPV.show(v);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastBack > 2000) {
                showMessage("再次按下返回键退出编辑");
                lastBack = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("InflateParams")
    @Override
    public void addTimeView(int layoutId) {
        View v = getLayoutInflater().inflate(R.layout.item_add_course_detail, null);
        if (firstView == null) {
            firstView = v;
        }
        v.setId(layoutId);
        v.findViewById(R.id.tv_week).setOnClickListener(this);
        v.findViewById(R.id.tv_time).setOnClickListener(this);
        if (editMode) {
            v.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.btn_delete).setOnClickListener(this);
        }
        timeFragment.addView(v);
    }

    @Override
    public String getAddressText(int layoutId) {
        View root = findViewById(layoutId);
        TextView tv = root.findViewById(R.id.et_address);
        return tv.getText().toString();
    }

    @Override
    public void setWeekOPVSelect(int op1, int op2, int op3) {
        weekOPV.setSelectOptions(op1, op2, op3);
        setWeekText(firstView.findViewById(R.id.tv_week), op1, op2, op3);
    }

    @Override
    public void setTimeOPVSelect(int op1, int op2, int op3) {
        timeOPV.setSelectOptions(op1, op2, op3);
        setTimeText(firstView.findViewById(R.id.tv_time), op1, op2, op3);
    }

    @Override
    public void editMode() {
        editMode = true;
        addBtn.setVisibility(View.GONE);
    }

    @Override
    public void setAddressText(String address) {
        EditText addressET = firstView.findViewById(R.id.et_address);
        addressET.setText(address);
    }
}
