package cn.ifafu.ifafu.mvp.syllabus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.jaeger.library.StatusBarUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.add_course.AddCourseActivity;
import cn.ifafu.ifafu.util.NumberUtils;
import cn.ifafu.ifafu.view.adapter.CoursePageAdapter;
import cn.ifafu.ifafu.view.dialog.CourseDetailDialog;
import cn.woolsen.android.mvp.BaseActivity;

public class SyllabusActivity extends BaseActivity<SyllabusContract.Presenter>
        implements SyllabusContract.View, CourseDetailDialog.OnClickListener, View.OnClickListener, View.OnLongClickListener {

    private ViewPager2 viewPager;

    private CoursePageAdapter adapter;

    private TextView cornerTV;

    private TextView subTitleTV;

    private CourseDetailDialog detailDialog;

    private int currentWeek = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);

        mPresenter = new SyllabusPresenter(this);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_refresh).setOnClickListener(this);
        TextView dateTV = findViewById(R.id.tv_date);
        dateTV.setText(new SimpleDateFormat("MM月dd日", Locale.CHINA).format(new Date()));

        detailDialog = new CourseDetailDialog(this);
        detailDialog.setOnClickListener(this);

        cornerTV = findViewById(R.id.tv_corner);
        viewPager = findViewById(R.id.view_pager);
        subTitleTV = findViewById(R.id.tv_sub_title);
        subTitleTV.setOnLongClickListener(this);
        adapter = new CoursePageAdapter(this);
        adapter.setCourserClickListener((v, course) -> detailDialog.show((Course) course.getOther()));
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position != currentWeek - 1) {
                    subTitleTV.setText(getString(R.string.week_format_not, NumberUtils.numberToChinese(position + 1)));
                } else {
                    subTitleTV.setText(getString(R.string.week_format, NumberUtils.numberToChinese(position + 1)));
                }
            }
        });
        mPresenter.onStart();
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
    public void onDeleteBtnClick(Course course) {
        showMessage("onDelete => " + course.getName());
    }

    @Override
    public void onEditBtnClick(Course course) {
        showMessage("onEditBtn => " + course.getName());
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                startActivityForResult(new Intent(this, AddCourseActivity.class), 0);
                break;
            case R.id.btn_refresh:
                mPresenter.updateSyllabus();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("course")) {
                Course course = (Course) data.getBundleExtra("course").get("course");
//                courseView.addCourse(course);
//                courseView.redraw();
            }
        }
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
            viewPager.setCurrentItem(week - 1);
        }
        currentWeek = week;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sub_title:
                viewPager.setCurrentItem(currentWeek - 1, true);
                return true;
        }
        return false;
    }
}
