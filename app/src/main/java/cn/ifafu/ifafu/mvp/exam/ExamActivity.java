package cn.ifafu.ifafu.mvp.exam;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;

import java.util.List;

import butterknife.BindView;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.WToolbar;
import cn.ifafu.ifafu.view.adapter.ExamAdapter;

public class ExamActivity extends BaseActivity<ExamContract.Presenter>
        implements ExamContract.View {

    @BindView(R.id.tb_exam)
    WToolbar tbExam;
    @BindView(R.id.rv_exam)
    RecyclerView rvExam;
    @BindView(R.id.btn_exam_refresh)
    ImageButton btnRefresh;

    private ExamAdapter examAdapter;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_exam;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        mPresenter = new ExamPresenter(this);
        btnRefresh.setOnClickListener(v -> mPresenter.update());
        tbExam = findViewById(R.id.tb_exam);
        tbExam.setSubtitle("2019-2020学年第1学期");
        tbExam.setSubtitleDrawablesRelative(
                null, null, getContext().getDrawable(R.drawable.ic_down_little), null
        );
        tbExam.getSubtitleTextView().setOnClickListener(v -> {

        });
        tbExam.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void setExamAdapterData(List<Exam> data) {
        Log.d(TAG, "exam size = " + data.size());
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
        } else {
            examAdapter.setExamData(data);
        }
    }

    @Override
    public void setSubtitle(String subtitle) {
        tbExam.setSubtitle(subtitle);
    }

    @Override
    public void showEmptyView() {
        rvExam.setVisibility(View.GONE);
//        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

}
