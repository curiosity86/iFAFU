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

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.mvp.syllabus_item.SyllabusItemActivity;
import cn.ifafu.ifafu.util.NumberUtils;
import cn.ifafu.ifafu.view.adapter.SyllabusPageAdapter;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class SyllabusActivity extends BaseActivity<SyllabusContract.Presenter>
        implements SyllabusContract.View, View.OnClickListener, View.OnLongClickListener {

    private ViewPager2 viewPager;

    private SyllabusPageAdapter adapter;

    private TextView cornerTV;

    private TextView subTitleTV;

//    private CourseDetailDialog detailDialog;

    private int mCurrentWeek = 1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);

        mPresenter = new SyllabusPresenter(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setText("加载中");
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_refresh).setOnClickListener(this);
        TextView dateTV = findViewById(R.id.tv_date);
        dateTV.setText(new SimpleDateFormat("MM月dd日", Locale.CHINA).format(new Date()));

//        detailDialog = new CourseDetailDialog(this);
//        detailDialog.setOnClickListener(this);

        cornerTV = findViewById(R.id.tv_corner);
        viewPager = findViewById(R.id.view_pager);
        subTitleTV = findViewById(R.id.tv_sub_title);
        subTitleTV.setOnLongClickListener(this);
        adapter = new SyllabusPageAdapter(this);
        adapter.setCourserClickListener((v, course) ->{
            Intent intent = new Intent(this, SyllabusItemActivity.class);
            intent.putExtra("course_id", ((Course) course.getOther()).getId());
            startActivityForResult(intent, 0x123);
        });
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                String numCN = NumberUtils.numberToChinese(position + 1);
                if (mCurrentWeek - 1 < 0) {
                    if (position != 0) {
                        subTitleTV.setText(getString(R.string.week_format_not_return, numCN, "第一周"));
                    } else {
                        subTitleTV.setText(getString(R.string.week_format_not, numCN));
                    }
                } else if (position != mCurrentWeek - 1) {
                    if (mCurrentWeek < 0) {
                        subTitleTV.setText(getString(R.string.week_format_not_return, numCN, "第一周"));
                    } else {
                        subTitleTV.setText(getString(R.string.week_format_not_return, numCN, "本周"));
                    }
                } else {
                    subTitleTV.setText(getString(R.string.week_format, numCN));
                }
            }
        });
        mPresenter.onStart();
    }

    @Override
    public void setFirstStudyDay(String firstStudyDay) {
        adapter.setDateOfFirstStudyDay(firstStudyDay);
    }

    @Override
    public void setSyllabusRowCount(int count) {
        //TODO
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                Intent intent = new Intent(this, SyllabusItemActivity.class);
                intent.putExtra("come_from", 1);
                startActivityForResult(intent, 0x123);
                break;
            case R.id.btn_refresh:
                mPresenter.updateSyllabus(true, true);
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0x123 && resultCode == Activity.RESULT_OK) {
            mPresenter.updateSyllabus(false, false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        adapter.setFirstDayOfWeek(firstDayOfWeek);
    }

    @Override
    public void setCornerText(String cornerText) {
        cornerTV.setText(cornerText);
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
        if (v.getId() == R.id.tv_sub_title) {
            viewPager.setCurrentItem(mCurrentWeek - 1, true);
            return true;
        }
        return false;
    }
}
