package cn.ifafu.ifafu.mvp.score;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.adapter.ScoreAdapter;
import cn.ifafu.ifafu.view.custom.EmptyView;
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider;
import cn.ifafu.ifafu.view.custom.WToolbar;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class ScoreActivity extends BaseActivity<ScoreContract.Presenter>
        implements ScoreContract.View {

    @BindView(R.id.tv_score_title)
    TextView tvScoreTitle;
    @BindView(R.id.tv_ies_big)
    TextView tvIesBig;
    @BindView(R.id.tv_ies_little)
    TextView tvIesLittle;
    @BindView(R.id.tv_cnt_big)
    TextView tvCntBig;
    @BindView(R.id.tv_cnt_little)
    TextView tvCntLittle;
    @BindView(R.id.tv_gpa)
    TextView tvGPA;
    @BindView(R.id.rv_score)
    RecyclerView rvScore;
    @BindView(R.id.view_exam_empty)
    EmptyView emptyView;

    private ScoreAdapter scoreAdapter;

    private ProgressDialog progressDialog;
    private OptionsPickerView<String> yearTermOPV;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_score;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        mPresenter = new ScorePresenter(this);

        WToolbar tb = findViewById(R.id.tb_score);
        tb.setOnClickListener(v -> finish());

        rvScore.setLayoutManager(new LinearLayoutManager(this));
        rvScore.addItemDecoration(new RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider));

        findViewById(R.id.btn_refresh).setOnClickListener(v -> mPresenter.update());

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void setYearTermOptions(int option1, int option2) {
        yearTermOPV.setSelectOptions(option1, option2);
    }

    @Override
    public void setRvScoreData(List<Score> data) {
        if (data.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            rvScore.setVisibility(View.GONE);
        } else {
            if (scoreAdapter == null) {
                emptyView.setVisibility(View.GONE);
                rvScore.setVisibility(View.VISIBLE);
                scoreAdapter = new ScoreAdapter(this, data);
                rvScore.setAdapter(scoreAdapter);
            } else {
                emptyView.setVisibility(View.GONE);
                rvScore.setVisibility(View.VISIBLE);
                scoreAdapter.setScoreData(data);
                scoreAdapter.notifyDataSetChanged();
            }
        }
        tvCntBig.setText(String.valueOf(data.size()));
        tvCntLittle.setText("门");
    }

    @Override
    public void setYearTermData(List<String> years, List<String> terms) {
        if (yearTermOPV == null) {
            yearTermOPV = new OptionsPickerBuilder(this,
                    (options1, options2, options3, v) -> {
                        setYearTermTitle(years.get(options1), terms.get(options2));
                        mPresenter.switchYearTerm(options1, options2);
                    })
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("请选择学年与学期")
                    .setTitleColor(Color.parseColor("#157efb"))
                    .setTitleSize(13)
                    .build();
        }
        yearTermOPV.setNPicker(years, terms, null);
    }

    @Override
    public void setIESText(String big, String little) {
        tvIesBig.setText(big);
        tvIesLittle.setText(little);
    }

    @Override
    public void setCntText(String big, String little) {
        tvCntBig.setText(big);
        tvCntLittle.setText(little);
    }

    @Override
    public void setGPAText(String text) {
        tvGPA.setText(text);
    }

    @Override
    public void setYearTermTitle(String year, String term) {
        if (term.equals("全部")) {
            tvScoreTitle.setText(String.format("%s学年全部学习成绩", year));
        } else {
            tvScoreTitle.setText(String.format("%s学年第%s学期学习成绩", year, term));
        }
    }

    @OnClick({R.id.tv_score_title, R.id.tv_cnt_big})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_score_title:
                yearTermOPV.show();
                break;
            case R.id.tv_cnt_big:
                mPresenter.openFilterActivity();
                break;
        }
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constant.SCORE_FILTER_ACTIVITY) {
            mPresenter.updateIES();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
