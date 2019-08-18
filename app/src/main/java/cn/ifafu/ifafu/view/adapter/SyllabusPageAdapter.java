package cn.ifafu.ifafu.view.adapter;

import android.content.Context;
import android.util.SparseArray;
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
import cn.ifafu.ifafu.view.syllabus.SyllabusView;
import cn.ifafu.ifafu.view.syllabus.data.DayOfWeek;

public class SyllabusPageAdapter extends RecyclerView.Adapter<SyllabusPageAdapter.VH> {

    private int mPageCount = 24;

    private SparseArray<List<Course>> mCourseList = new SparseArray<>();

    private String[] mBeginTimes;

    private Context mContext;

    private int firstDayOfWeek = Calendar.SUNDAY;

    private CourseView.OnCourseClickListener onCourseClickListener;

    private String dateOfFirstStudyDay;

    public SyllabusPageAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        l("onCreateViewHolder");
//        View syllabusView = LayoutInflater.from(mContext).inflate(R.layout.fragment_syllabus, parent, false);
//        return new ViewPagerViewHolder(syllabusView);
        View view = new SyllabusView(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        view.setLayoutParams(lp);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SyllabusView view = holder.syllabusView;
        view.setBeginTimeTexts(mBeginTimes);
        view.setFirstDayOfWeek(firstDayOfWeek);
        view.setOnCourseClickListener(onCourseClickListener);
        view.setCourseData(mCourseList.get(position));
        view.setDateTexts(DateUtils.getWeekDates(dateOfFirstStudyDay, position, firstDayOfWeek, "MM-dd"));
        view.redraw();
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

    public void setCornerText(String text) {

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

    class VH extends ViewPagerViewHolder {

        SyllabusView syllabusView;

        VH(@NonNull View syllabusView) {
            super(syllabusView);
            this.syllabusView = (SyllabusView) syllabusView;
        }
    }

}
