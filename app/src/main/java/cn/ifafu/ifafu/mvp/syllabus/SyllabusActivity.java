package cn.ifafu.ifafu.mvp.syllabus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.jaeger.library.StatusBarUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.mvp.syllabus_item.SyllabusItemActivity;
import cn.ifafu.ifafu.mvp.syllabus_setting.SyllabusSettingActivity;
import cn.ifafu.ifafu.util.ChineseNumbers;
import cn.ifafu.ifafu.view.adapter.SyllabusPageAdapter;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class SyllabusActivity extends BaseActivity<SyllabusContract.Presenter>
        implements SyllabusContract.View, View.OnLongClickListener {

    @BindView(R.id.view_pager)
    ViewPager2 viewPager;
    @BindView(R.id.tv_subtitle)
    TextView tvSubtitle;

    private SyllabusPageAdapter adapter;

    private int mCurrentWeek = 1;

    private ProgressDialog progressDialog;

    public static final int ADD = 0;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_syllabus;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);

        mPresenter = new SyllabusPresenter(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setText("加载中");
        TextView dateTV = findViewById(R.id.tv_date);
        dateTV.setText(new SimpleDateFormat("MM月dd日", Locale.CHINA).format(new Date()));

        viewPager = findViewById(R.id.view_pager);
        tvSubtitle.setOnLongClickListener(this);
        adapter = new SyllabusPageAdapter(this);
        adapter.setCourserClickListener((v, course) ->{
            Intent intent = new Intent(this, SyllabusItemActivity.class);
            intent.putExtra("course_id", ((Course) course.getOther()).getId());
            startActivityForResult(intent, Constant.SYLLABUS_ITEM_ACTIVITY);
        });
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                String numCN = ChineseNumbers.englishNumberToChinese(String.valueOf(position + 1));
                if (mCurrentWeek - 1 < 0) {
                    if (position != 0) {
                        tvSubtitle.setText(getString(R.string.week_format_not_return, numCN, "第一周"));
                    } else {
                        tvSubtitle.setText(getString(R.string.week_format_not, numCN));
                    }
                } else if (position != mCurrentWeek - 1) {
                    if (mCurrentWeek < 0) {
                        tvSubtitle.setText(getString(R.string.week_format_not_return, numCN, "第一周"));
                    } else {
                        tvSubtitle.setText(getString(R.string.week_format_not_return, numCN, "本周"));
                    }
                } else {
                    tvSubtitle.setText(getString(R.string.week_format, numCN));
                }
            }
        });
    }

    @Override
    public void setFirstStudyDay(String firstStudyDay) {
        adapter.setDateOfFirstStudyDay(firstStudyDay);
    }

    @Override
    public void setSyllabusRowCount(int count) {

    }

    @Override
    public void setCourseBeginTime(String[] times) {
        adapter.setSideViewBeginTime(times);
    }

    @Override
    public void setSyllabusDate(List<Course> courses) {
        adapter.setCourseList(courses);
    }

    @Override
    public void redrawSyllabus() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.cancel();
    }

    @OnClick({R.id.btn_add, R.id.btn_refresh, R.id.btn_back, R.id.btn_setting})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                Intent intent = new Intent(this, SyllabusItemActivity.class);
                intent.putExtra("come_from", ADD);
                startActivityForResult(intent, Constant.SYLLABUS_ITEM_ACTIVITY);
                break;
            case R.id.btn_refresh:
                mPresenter.updateSyllabusNet();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_setting:
                startActivityForResult(new Intent(this, SyllabusSettingActivity.class), 0x123);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constant.SYLLABUS_ITEM_ACTIVITY && resultCode == Activity.RESULT_OK) {
            mPresenter.updateSyllabusLocal();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        adapter.setFirstDayOfWeek(firstDayOfWeek);
    }

    @Override
    public void setCornerText(String cornerText) {
        adapter.setCornerText(cornerText);
    }

    @Override
    public void setCurrentWeek(int week) {
        if (viewPager.getCurrentItem() != week - 1) {
            viewPager.setCurrentItem(week - 1, false);
        }
        mCurrentWeek = week;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.tv_subtitle) {
            viewPager.setCurrentItem(mCurrentWeek - 1, true);
            return true;
        }
        return false;
    }
}
