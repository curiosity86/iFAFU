package cn.ifafu.ifafu.mvp.score_filter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.adapter.ScoreFilterAdapter;
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider;
import cn.ifafu.ifafu.view.custom.WToolbar;

public class ScoreFilterActivity extends BaseActivity<ScoreFilterConstant.Presenter> implements ScoreFilterConstant.View {

    @BindView(R.id.tb_score_filter)
    WToolbar tbScoreFilter;
    @BindView(R.id.tv_now_ies)
    TextView tvNowIes;
    @BindView(R.id.rv_score_filter)
    RecyclerView rvScoreFilter;

    private ScoreFilterAdapter mAdapter;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_score_filter;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        mPresenter = new ScoreFilterPresenter(this);
        tbScoreFilter.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void setAdapterData(List<Score> list) {
        if (mAdapter == null) {
            mAdapter = new ScoreFilterAdapter(this, list);
            mAdapter.setOnCheckedListener((v, score, isChecked) -> {
                score.setIsIESItem(isChecked);
                mPresenter.updateIES();
            });
            rvScoreFilter.setAdapter(mAdapter);
            rvScoreFilter.setLayoutManager(new LinearLayoutManager(this));
            rvScoreFilter.addItemDecoration(new RecyclerViewDivider(
                    this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider));
        } else {
            mAdapter.setData(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setIES(String ies) {
        tvNowIes.setText(getString(R.string.score_filter_now_ies, ies));
    }

    @Override
    public List<Score> getAdapterData() {
        return mAdapter.getData();
    }

    @OnClick(R.id.btn_filter_all)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_filter_all:
                mAdapter.setAllChecked();
                mAdapter.notifyDataSetChanged();
                mPresenter.updateIES();
                break;
        }
    }
}
