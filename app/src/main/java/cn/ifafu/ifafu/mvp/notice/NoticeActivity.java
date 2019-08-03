package cn.ifafu.ifafu.mvp.notice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.mvp.other.WebActivity;
import cn.ifafu.ifafu.data.entity.Notice;
import cn.woolsen.android.mvp.BaseActivity;

public class NoticeActivity extends BaseActivity<NoticeContract.Presenter>
        implements NoticeContract.View {

    private LinearLayout mInformLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);

        mPresenter = new NoticePresenter(this);

        mInformLayout = findViewById(R.id.ll_inform);
        Toolbar toolbar = findViewById(R.id.tb_notice);
        toolbar.setNavigationOnClickListener(v -> finish());

        mPresenter.onStart();
    }

    private void addInformItem(List<Notice> noticeList) {
        for (Notice notice : noticeList) {
            final String title = notice.getTitle();
            final String url = notice.getUrl();
//            final NoticeLayout informLayout = new NoticeLayout(this);
//            informLayout.setTitle(notice.getTitle());
//            informLayout.setContent(notice.getDate());
//            informLayout.setOnClickListener(v -> {
//                gotoBrowser(title, url);
//                informLayout.setRedDotGone();
//            });
//            this.mInformLayout.addView(informLayout);
        }
    }

    private void gotoBrowser(String title, String url) {
        Intent intent = new Intent(NoticeActivity.this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("pageTitle", title);
        bundle.putString("loadUrl", url);
        bundle.putBoolean("js", false);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showNotices(List<Notice> notices) {
        mInformLayout.removeAllViews();
        addInformItem(notices);
    }
}
