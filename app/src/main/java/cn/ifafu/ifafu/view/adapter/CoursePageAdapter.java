package cn.ifafu.ifafu.view.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.util.DateUtils;
import cn.ifafu.ifafu.view.syllabus.CourseView;
import cn.ifafu.ifafu.view.syllabus.DateView;
import cn.ifafu.ifafu.view.syllabus.SideView;
import cn.ifafu.ifafu.view.syllabus.data.DayOfWeek;

public class CoursePageAdapter extends RecyclerView.Adapter<CoursePageAdapter.ViewPagerViewHolder> {

    private int mPageCount = 24;

    private SparseArray<List<Course>> mCourseList = new SparseArray<>();

    private String[] mBeginTimes;

    private Context mContext;

    private int firstDayOfWeek = Calendar.SUNDAY;

    private CourseView.OnCourseClickListener onCourseClickListener;

    private String dateOfFirstStudyDay;

    //TODO 在5.1系统上，CourseView在绘制时会测量两次，在测量第二次时高度会出错，目前未能发现是啥原因

    public CoursePageAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        l("onCreateViewHolder");
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_syllabus, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
//        l("onBindViewHolder");
        if (holder.sideView.getBeginTimeTexts() != mBeginTimes) {
            holder.sideView.setBeginTimeTexts(mBeginTimes);
        }
        holder.courseView.setFirstDayOfWeek(firstDayOfWeek);
        holder.courseView.setOnCourseClickListener((v, course) -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onClick(v, course);
            }
        });
        if (mCourseList.get(position) != null) {
            holder.courseView.setCourses(mCourseList.get(position));
            holder.courseView.redraw();
        }
        holder.dateView.setFirstDayOfWeek(firstDayOfWeek);
        if (dateOfFirstStudyDay != null) {
            holder.dateView.setDateTexts(DateUtils.getWeekDates(dateOfFirstStudyDay, position, firstDayOfWeek, "MM-dd"));
            holder.dateView.redraw();
        }
    }

    /**
     * @param dateOfFirstStudyDay example: "2019-09-01"
     */
    public void setDateOfFirstStudyDay(String dateOfFirstStudyDay) {
        this.dateOfFirstStudyDay = dateOfFirstStudyDay;
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
     * @param times times
     */
    public void setSideViewBeginTime(String[] times) {
        mBeginTimes = times;
    }

    /**
     * 修改后需调用{@link #notifyDataSetChanged()}，才会更新布局
     *
     * @param courseList courses
     */
    public void setCourseList(List<Course> courseList) {
        mCourseList.clear();
        for (Course course : courseList) {
            for (int week : course.getWeekSet()) {
                addCourse(week - 1, course);
            }
        }
    }

    private void addCourse(int index, Course course) {
        if (mCourseList.get(index) == null) {
            mCourseList.put(index, new ArrayList<>());
        }
        mCourseList.get(index).add(course);
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

        TextView cornerView;

        ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseView = itemView.findViewById(R.id.view_course);
            sideView = itemView.findViewById(R.id.view_side);
            dateView = itemView.findViewById(R.id.view_date);
            cornerView = itemView.findViewById(R.id.tv_corner);
        }
    }

    private void l(CourseView view, Object... msg) {
        String code = view.toString();
        code = code.substring(code.indexOf("{") + 1, code.indexOf(" V.E"));
        StringBuilder sb = new StringBuilder(code);
        sb.append("    ");
        for (Object s : msg) {
            sb.append(s).append(" ");
        }
//        Log.d("Syllabus", sb.toString());
    }

    private void l(Object... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (Object s : msg) {
            sb.append(s).append(" ");
        }
//        Log.d("Syllabus", sb.toString());
    }
}
