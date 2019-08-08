package cn.ifafu.ifafu.mvp.exam;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;

import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.view.adapter.ExamAdapter;
import cn.ifafu.ifafu.mvp.base.BaseActivity;

public class ExamActivity extends BaseActivity<ExamContract.Presenter>
        implements ExamContract.View {

    private Toolbar mToolbar;
    private RecyclerView mExamRecycleView;
    private View emptyView;

    private ExamAdapter examAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        mPresenter = new ExamPresenter(this);
        mExamRecycleView = findViewById(R.id.rv_exam);
        mToolbar = findViewById(R.id.tb_exam);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mPresenter.onStart();
    }

    @Override
    public void setExamAdapterData(List<Exam> data) {
        if (examAdapter == null) {
            examAdapter = new ExamAdapter(this, data);
            mExamRecycleView.setLayoutManager(new LinearLayoutManager(this));
            mExamRecycleView.setAdapter(examAdapter);
        } else {
            examAdapter.setExamData(data);
        }
    }

    @Override
    public void setSubtitle(String subtitle) {
        mToolbar.setSubtitle(subtitle);
    }

    @Override
    public void showEmptyView() {
        mExamRecycleView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
