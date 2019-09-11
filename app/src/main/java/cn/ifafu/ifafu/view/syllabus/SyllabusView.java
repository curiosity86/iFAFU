package cn.ifafu.ifafu.view.syllabus;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.List;

import cn.ifafu.ifafu.R;

public class SyllabusView extends LinearLayout {

    private CourseView courseView;
    private SideView sideView;
    private DateView dateView;
    private TextView cornerView;

    private boolean courseViewChange = false;
    private boolean sideViewChange = false;
    private boolean dateViewChange = false;

    public SyllabusView(Context context) {
        this(context, null);
    }

    public SyllabusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SyllabusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.fragment_syllabus, null, false);
        courseView = rootView.findViewById(R.id.view_course);
        sideView = rootView.findViewById(R.id.view_side);
        dateView = rootView.findViewById(R.id.view_date);
        cornerView = rootView.findViewById(R.id.tv_corner);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        addView(rootView, lp);
    }

    public void setCourseTextSize(int textSize) {
        courseView.setTextSize(textSize);
    }

    public void setCourseData(List<? extends ToCourseBase> courses) {
        courseView.setCourses(courses);
        courseViewChange = true;
    }

    public void setBeginTimeTexts(String[] beginTimeTexts) {
        if (beginTimeTexts != sideView.getBeginTimeTexts()) {
            sideView.setBeginTimeTexts(beginTimeTexts);
            sideViewChange = true;
        }
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        courseView.setFirstDayOfWeek(firstDayOfWeek);
        dateView.setFirstDayOfWeek(firstDayOfWeek);
        courseViewChange = true;
        dateViewChange = true;
    }

    public void setCornerText(@StringRes int resId) {
        cornerView.setText(getContext().getText(resId));
    }

    public void setCornerText(CharSequence text) {
        cornerView.setText(text);
    }

    public void setRowCount(int rowCount) {
        sideView.setRowCount(rowCount);
        courseView.setRowCount(rowCount);
    }

    public void setOnCourseClickListener(CourseView.OnCourseClickListener listener) {
        courseView.setOnCourseClickListener(listener);
    }

    public void setDateTexts(String[] dateTexts) {
        dateView.setDateTexts(dateTexts);
        dateViewChange = true;
    }

    public void setShowHorizontalDivider(boolean isShow) {
        sideView.setShowHorizontalDivider(isShow);
        courseView.setShowHorizontalDivider(isShow);
    }

    public void setShowVerticalDivider(boolean isShow) {
        dateView.setShowVerticalDivider(isShow);
        courseView.setShowVerticalDivider(isShow);
    }

    public void redraw() {
        if (courseViewChange) {
            courseView.redraw();
            courseViewChange = false;
        }
        if (sideViewChange) {
            sideView.redraw();
            sideViewChange = false;
        }
        if (dateViewChange) {
            dateView.redraw();
            dateViewChange = false;
        }
    }

}
