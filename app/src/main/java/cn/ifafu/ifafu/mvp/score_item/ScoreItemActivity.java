package cn.ifafu.ifafu.mvp.score_item;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;

import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.BaseActivity;

public class ScoreItemActivity extends BaseActivity<ScoreItemConstant.Presenter> implements ScoreItemConstant.View{

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_score_item;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
    }
}
