package cn.ifafu.ifafu.electricity.main;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.gyf.immersionbar.ImmersionBar;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.custom.WToolbar;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class ElecMainActivity extends BaseActivity<ElecMainContract.Presenter>
        implements ElecMainContract.View, RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnClickListener {

    private ProgressDialog progress;

    private SparseArray<String> viewIdToNameMap;  //记录RadioButtonId对应的电控名字

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.xqTV)
    TextView areaTv;
    @BindView(R.id.ldTV)
    TextView buildingTv;
    @BindView(R.id.lcTV)
    TextView floorTv;
    @BindView(R.id.elecTV)
    TextView elecTv;
    @BindView(R.id.accountTV)
    TextView snoTv;
    @BindView(R.id.balanceTV)
    TextView balanceTv;
    @BindView(R.id.priceET)
    EditText priceEt;
    @BindView(R.id.roomET)
    EditText roomEt;
    @BindView(R.id.payBtn)
    Button payBtn;
    @BindView(R.id.tb_elec)
    WToolbar tbElec;

    private OptionsPickerView<String> xqOpv;
    private OptionsPickerView<String> ldOpv;
    private OptionsPickerView<String> lcOpv;

    private List<String> xqData;
    private List<String> ldData;
    private List<String> lcData;

    private AlertDialog confirmDialog;

    private String lastRoomText = "";

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_elec_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tbElec)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init();

        mPresenter = new ElecMainPresenter(this);

        WToolbar tbElec = findViewById(R.id.tb_elec);
        tbElec.setNavigationOnClickListener(v -> finish());

        viewIdToNameMap = new SparseArray<>();

        progress = new ProgressDialog(this);
        progress.setText("加载中");

        radioGroup.setOnCheckedChangeListener(this);

        xqOpv = new OptionsPickerBuilder(this,
                (options1, options2, options3, v) -> {
                    mPresenter.onAreaSelect(xqData.get(options1));
                    areaTv.setText(xqData.get(options1));
                })
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择校区")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build();

        ldOpv = new OptionsPickerBuilder(this,
                (options1, options2, options3, v) -> {
                    mPresenter.onBuildingSelect(ldData.get(options1));
                    buildingTv.setText(ldData.get(options1));
                })
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择楼栋")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build();

        lcOpv = new OptionsPickerBuilder(this,
                (options1, options2, options3, v) -> {
                    mPresenter.onFloorSelect(lcData.get(options1));
                    floorTv.setText(lcData.get(options1));
                })
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择楼层")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build();

        confirmDialog = new AlertDialog.Builder(this)
                .setPositiveButton("确认", this)
                .setNegativeButton("取消", this)
                .create();

        roomEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !lastRoomText.equals(roomEt.getText().toString())) {
                elecTv.setText(R.string.query_elec);
                priceEt.setVisibility(View.GONE);
                priceEt.setText("");
                payBtn.setVisibility(View.GONE);
            }
        });

        initViewVisibility();
    }

    @Override
    public void setBalanceText(String text) {
        balanceTv.setText(getString(R.string.balance, text));
    }

    public void setSelectorView(int i, List<String> list) {
        if (i == 1) {
            areaTv.setVisibility(View.VISIBLE);
            xqData = list;
            xqOpv.setPicker(list);
        } else if (i == 2) {
            buildingTv.setVisibility(View.VISIBLE);
            ldData = list;
            ldOpv.setPicker(list);
        } else if (i == 3) {
            floorTv.setVisibility(View.VISIBLE);
            lcData = list;
            lcOpv.setPicker(list);
        }
    }

    @Override
    public void setSelections(@Nullable String dkName, @Nullable String area, @Nullable String building, @Nullable String floor) {
        radioGroup.check(viewIdToNameMap.keyAt(viewIdToNameMap.indexOfValue(dkName)));
        if (area != null && !area.isEmpty()) {
            areaTv.setText(area);
            areaTv.setVisibility(View.VISIBLE);
        }
        if (building != null && !building.isEmpty()) {
            buildingTv.setText(building);
            buildingTv.setVisibility(View.VISIBLE);
        }
        if (floor != null && !floor.isEmpty()) {
            floorTv.setText(floor);
            floorTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) {
            mPresenter.pay();
        }
        dialog.cancel();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        Log.d("ElecMainActivity", "onCheckedChanged ==> " + checkedId + "  " + viewIdToNameMap.get(checkedId));
        if (viewIdToNameMap.get(checkedId) != null) {
            mPresenter.onDKSelect(viewIdToNameMap.get(checkedId));
        }
    }

    @Override
    public void initViewVisibility() {
        areaTv.setVisibility(View.GONE);
        areaTv.setText("");
        buildingTv.setVisibility(View.GONE);
        buildingTv.setText("");
        floorTv.setVisibility(View.GONE);
        floorTv.setText("");
        roomEt.setVisibility(View.GONE);
        roomEt.setText("");
        elecTv.setVisibility(View.GONE);
        elecTv.setText("");
        priceEt.setVisibility(View.GONE);
        priceEt.setText("");
        payBtn.setVisibility(View.GONE);
    }

    @Override
    public void setElecText(String text) {
        elecTv.setText(text);
    }


    @OnClick({R.id.quitTV, R.id.balanceBtn, R.id.xqTV, R.id.ldTV, R.id.lcTV, R.id.elecTV, R.id.payBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.quitTV:
                mPresenter.quit();
                break;
            case R.id.balanceBtn:
                mPresenter.queryCardBalance();
                break;
            case R.id.xqTV:
                xqOpv.show();
                break;
            case R.id.ldTV:
                ldOpv.show();
                break;
            case R.id.lcTV:
                lcOpv.show();
                break;
            case R.id.elecTV:
                mPresenter.queryElecBalance();
                break;
            case R.id.payBtn:
                mPresenter.whetherPay();
                break;
        }
    }

    @Override
    public void setSnoText(String text) {
        snoTv.setText(text);
    }

    @Override
    public void showLoading() {
        progress.show();
    }

    @Override
    public void showPayView() {
        priceEt.setVisibility(View.VISIBLE);
        payBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void showConfirmDialog(String message) {
        confirmDialog.setMessage(message);
        confirmDialog.show();
    }

    @Override
    public void showElecCheckView() {
        roomEt.setVisibility(View.VISIBLE);
        elecTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progress.cancel();
    }

    @Override
    public void showDKSelection(Collection<String> strings) {
        for (String s : strings) {
            RadioButton rb1 = new RadioButton(this);
            RadioGroup.LayoutParams rgLp = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            rb1.setLayoutParams(rgLp);
            rb1.append(s);
            int id = View.generateViewId();
            rb1.setId(id);
            viewIdToNameMap.append(id, s);
            radioGroup.addView(rb1);
        }
    }

    @Override
    public void setRoomText(String text) {
        lastRoomText = text;
        roomEt.setText(text);
    }

    public String getCheckedDKName() {
        return viewIdToNameMap.get(radioGroup.getCheckedRadioButtonId());
    }

    @Override
    public String getAreaText() {
        return areaTv.getText().toString();
    }

    @Override
    public String getBuildingText() {
        return buildingTv.getText().toString();
    }

    @Override
    public String getFloorText() {
        return floorTv.getText().toString();
    }

    @Override
    public String getPriceText() {
        return priceEt.getText().toString();
    }

    @Override
    public String getRoomText() {
        return roomEt.getText().toString();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {//点击editText控件外部
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    if (roomEt != null) {
                        roomEt.clearFocus();
                    }
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

}
