package cn.ifafu.ifafu.mvp.exam;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

import butterknife.BindView;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.base.BaseActivity;
import cn.ifafu.ifafu.view.adapter.ExamAdapter;
import cn.ifafu.ifafu.view.custom.EmptyView;
import cn.ifafu.ifafu.view.custom.WToolbar;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class ExamActivity extends BaseActivity<ExamContract.Presenter>
        implements ExamContract.View {

    @BindView(R.id.tb_exam)
    WToolbar tbExam;
    @BindView(R.id.rv_exam)
    RecyclerView rvExam;
    @BindView(R.id.btn_exam_refresh)
    ImageButton btnRefresh;
    @BindView(R.id.view_exam_empty)
    EmptyView emptyView;

    private ExamAdapter examAdapter;

    private ProgressDialog progressDialog;
    private OptionsPickerView<String> yearTermOPV;

    private List<String> years;
    private List<String> terms;

    public int getLayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_exam;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tbExam)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init();
        mPresenter = new ExamPresenter(this);
        progressDialog = new ProgressDialog(this);

        btnRefresh.setOnClickListener(v -> mPresenter.update());
        tbExam = findViewById(R.id.tb_exam);
        tbExam.setNavigationOnClickListener(v -> finish());

    }

    @Override
    public void setExamAdapterData(List<Exam> data) {
        if (examAdapter == null) {
            examAdapter = new ExamAdapter(this, data);
            rvExam.setLayoutManager(new LinearLayoutManager(this));
            DividerItemDecoration dividerItemDecoration =
                    new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            Drawable divider = getContext().getDrawable(R.drawable.shape_divider);
            if (divider != null) {
                dividerItemDecoration.setDrawable(divider);
            }
            rvExam.addItemDecoration(dividerItemDecoration);
            rvExam.setAdapter(examAdapter);
        }

        isShowEmptyView(data.isEmpty());
        if (!data.isEmpty()) {
            examAdapter.setExamData(data);
            examAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setYearTermData(final List<String> years, final List<String> terms) {
        this.years = years;
        this.terms = terms;
        if (yearTermOPV == null) {
            yearTermOPV = new OptionsPickerBuilder(this,
                    (options1, options2, options3, v) -> {
                        setSubtitle(years.get(options1), terms.get(options2));
                        mPresenter.switchYearTerm(options1, options2);
                    })
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("请选择学年与学期")
                    .setTitleColor(Color.parseColor("#157efb"))
                    .setTitleSize(13)
                    .build();
            tbExam.setSubtitleClickListener(v -> {
                yearTermOPV.show();
            });
            tbExam.setSubtitleDrawablesRelative(
                    null, null, getContext().getDrawable(R.drawable.ic_down_little), null
            );
        }
        yearTermOPV.setNPicker(years, terms, null);
    }

    private void isShowEmptyView(boolean show) {
        if (show) {
            emptyView.setVisibility(View.VISIBLE);
            rvExam.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            rvExam.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void showEmptyView() {
        isShowEmptyView(true);
    }

    @Override
    public void setYearTermOptions(int option1, int option2) {
        yearTermOPV.setSelectOptions(option1, option2);
        setSubtitle(years.get(option1), terms.get(option2));
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.cancel();
    }


    private void setSubtitle(String year, String term) {
        tbExam.setSubtitle(String.format("%s学年第%s学期", year, term));
    }


}
