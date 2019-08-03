package cn.ifafu.ifafu.mvp.add_course;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.woolsen.android.mvp.BaseActivity;

public class AddCourseActivity extends BaseActivity<AddCourseContract.Presenter>
        implements AddCourseContract.View, View.OnClickListener {

    private OptionsPickerView<String> weekOPV;
    private OptionsPickerView<String> timeOPV;

    private TextView nameTV;
    private TextView teacherTV;
    private LinearLayout timeFragment;

    private long lastBack = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        StatusBarUtil.setColor(this, getColor(R.color.global_blue), 0);

        nameTV = findViewById(R.id.et_name);
        teacherTV = findViewById(R.id.et_teacher);
        timeFragment = findViewById(R.id.fragment_time);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_finish).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        weekOPV = new OptionsPickerBuilder(this,
                (options1, options2, options3, v) -> {
                    if (options2 > options3) {
                        options3 = options2;
                    }
                    weekOPV.setSelectOptions(options1, options2, options3);
                    ((TextView) v).setText(mPresenter.onWeekSelect(
                            options1, options2, options3, ((View) v.getParent().getParent()).getId()
                    ));
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
                    ((TextView) v).setText(mPresenter.onTimeSelect(
                            options1, options2, options3, (((View) v.getParent().getParent()).getId())
                    ));
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
        mPresenter = new AddCoursePresenter(this);
        mPresenter.onAdd();
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
        v.setId(layoutId);
        v.findViewById(R.id.tv_week).setOnClickListener(this);
        v.findViewById(R.id.tv_time).setOnClickListener(this);
        v.findViewById(R.id.tv_room).setOnClickListener(this);
        v.findViewById(R.id.btn_delete).setOnClickListener(this);
        timeFragment.addView(v);
    }

    @Override
    public String getAddressText(int layoutId) {
        View root = findViewById(layoutId);
        TextView tv = root.findViewById(R.id.tv_room);
        return tv.getText().toString();
    }
}
