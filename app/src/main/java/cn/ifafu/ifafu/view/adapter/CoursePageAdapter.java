package cn.ifafu.ifafu.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.woolsen.android.uitl.DateUtils;
import cn.woolsen.android.view.syllabus.CourseView;
import cn.woolsen.android.view.syllabus.DateView;
import cn.woolsen.android.view.syllabus.SideView;
import cn.woolsen.android.view.syllabus.data.DayOfWeek;

public class CoursePageAdapter extends RecyclerView.Adapter<CoursePageAdapter.ViewPagerViewHolder> {

    private int mPageCount = 24;

    private List<Course>[] mCourseList;

    private String[] beginTimes;

    private Context mContext;

    private int firstDayOfWeek = Calendar.SUNDAY;

    private CourseView.OnCourseClickListener onCourseClickListener;

    private String dateOfFirstStudyDay = "2019-09-01";

    public CoursePageAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_syllabus, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        if (beginTimes != null) {
            holder.sideView.setBeginTimeTexts(beginTimes);
        }
        Log.d("CoursePageAdapter", position + "     course view = " + holder.courseView.hashCode());
        holder.courseView.setFirstDayOfWeek(firstDayOfWeek);
        holder.courseView.setOnCourseClickListener((v, course) -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onClick(v, course);
            }
        });
        if (mCourseList != null && position < mCourseList.length && mCourseList[position] != null) {
            holder.courseView.setCourses(mCourseList[position]);
            holder.courseView.redraw();
        }
        holder.dateView.setFirstDayOfWeek(firstDayOfWeek);
        holder.dateView.setDateTexts(DateUtils.getWeekDates(dateOfFirstStudyDay, position, firstDayOfWeek, "MM-dd"));
    }

    @Override
    public int getItemCount() {
        return mPageCount;
    }

    public void setPageCount(int pageCount) {
        int old = mPageCount;
        mPageCount = pageCount;
        if (mPageCount > old) {
            notifyItemRangeChanged(old, old - pageCount);
        } else {
            notifyItemMoved(pageCount, old - 1);
        }
    }

    /**
     * 修改后需调用{@link #notifyDataSetChanged()}，才会更新布局
     *
     * @param times
     */
    public void setSideViewBeginTime(String[] times) {
        beginTimes = times;
    }

    /**
     * 修改后需调用{@link #notifyDataSetChanged()}，才会更新布局
     *
     * @param courseList courses
     */
    public void setCourseList(List<Course> courseList) {
        mCourseList = new List[mPageCount];
        for (int i = 0; i < mPageCount; i++) {
            mCourseList[i] = new ArrayList<>();
        }
        for (Course course : courseList) {
            if (course.getWeekType() == Course.SINGLE_WEEK) {
                int beginWeek = course.getBeginWeek() % 2 == 1 ? course.getBeginWeek() : course.getBeginWeek() + 1;
                for (int i = beginWeek - 1; i < course.getEndWeek(); i += 2) {
                    mCourseList[i].add(course);
                }
            } else if (course.getWeekType() == Course.DOUBLE_WEEK) {
                int beginWeek = course.getBeginWeek() % 2 == 0 ? course.getBeginWeek() : course.getBeginWeek() + 1;
                for (int i = beginWeek - 1; i < course.getEndWeek(); i += 2) {
                    mCourseList[i].add(course);
                }
            } else if (course.getWeekType() == Course.ALL_WEEK) {
                for (int i = course.getBeginWeek() - 1; i < course.getEndWeek(); i++) {
                    mCourseList[i].add(course);
                }
            }
        }
    }

    public void setCourserClickListener(CourseView.OnCourseClickListener listener) {
        onCourseClickListener = listener;
    }

    public void setFirstDayOfWeek(@DayOfWeek int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    class ViewPagerViewHolder extends RecyclerView.ViewHolder {

        CourseView courseView;

        SideView sideView;

        DateView dateView;

        ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseView = itemView.findViewById(R.id.view_course);
            sideView = itemView.findViewById(R.id.view_side);
            dateView = itemView.findViewById(R.id.view_date);
        }
    }
}
