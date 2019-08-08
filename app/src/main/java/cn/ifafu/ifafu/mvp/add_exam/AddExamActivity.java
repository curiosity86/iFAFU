package cn.ifafu.ifafu.mvp.add_exam;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.ifafu.ifafu.mvp.base.BaseActivity;

public class AddExamActivity extends BaseActivity<AddExamContract.Presenter> implements AddExamContract.View {

    private TimePickerView mTimePickerView;

    private EditText nameET;
    private TextView dateET;
    private EditText addressET;
    private EditText seatET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);

        mPresenter = new AddExamPresenter(this);

        nameET = findViewById(R.id.et_name);
        dateET = findViewById(R.id.tv_time);
        addressET = findViewById(R.id.et_address);
        seatET = findViewById(R.id.et_seat);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日（HH:mm）", Locale.CHINA);

        Toolbar toolbar = findViewById(R.id.tb_add_exam);
        toolbar.setNavigationOnClickListener(v -> finish());

        mTimePickerView = new TimePickerBuilder(this,
                ((date, v) -> {
                    String dateText = dateFormat.format(date);
                    showMessage(dateText);
                    dateET.setText(dateText);
                }))
                .setType(new boolean[]{true, true, true, true, true, false})
                .setSubmitText("确定")
                .setCancelText("取消")
                .setTitleText("请选择时间")
                .build();

        findViewById(R.id.tv_time).setOnClickListener(v -> {
            GlobalLib.hideSoftKeyboard(this);
            mTimePickerView.show();
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> mPresenter.onSave());
    }

    @Override
    public String getNameText() {
        return nameET.getText().toString();
    }

    @Override
    public String getAddressText() {
        return addressET.getText().toString();
    }

    @Override
    public String getSeatText() {
        return seatET.getText().toString();
    }

    @Override
    public String getDateText() {
        return dateET.getText().toString();
    }
}
