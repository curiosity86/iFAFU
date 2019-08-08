package cn.ifafu.ifafu.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.util.DateUtils;

public class CourseDetailDialog extends Dialog {

    private TextView nameTv;
    private TextView teacherTv;
    private TextView addressTv;
    private TextView weekTv;
    private TextView timeTv;

    private ImageButton deleteIb;
    private ImageButton editIb;

    private OnClickListener listener;

    public CourseDetailDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_course_detail);
        nameTv = findViewById(R.id.nameTv);
        teacherTv = findViewById(R.id.teacherTv);
        addressTv = findViewById(R.id.addressTv);
        weekTv = findViewById(R.id.weeksTv);
        timeTv = findViewById(R.id.timeTv);
        deleteIb = findViewById(R.id.deleteIb);
        editIb = findViewById(R.id.editIb);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("DefaultLocale")
    public void show(final Course course) {
        super.show();
        nameTv.setText(course.getName());
        addressTv.setText(course.getAddress());
        teacherTv.setText(course.getTeacher());
        String weekType;
        if (course.getWeekType() == Course.SINGLE_WEEK) {
            weekType = "单周";
        } else if (course.getWeekType() == Course.DOUBLE_WEEK) {
            weekType = "双周";
        } else {
            weekType = "";
        }
        weekTv.setText(String.format("%s 第%d - %d周 %s",
                DateUtils.getWeekdayCN(course.getWeekday()), course.getBeginWeek(), course.getEndWeek(), weekType));
        timeTv.setText(String.format("%s 第%d - %d节",
                DateUtils.getWeekdayCN(course.getWeekday()), course.getBeginNode(),
                course.getBeginNode() + course.getNodeCnt() - 1));
        if (listener != null) {
            deleteIb.setOnClickListener(v -> listener.onDeleteBtnClick(this, course));
            editIb.setOnClickListener(v -> listener.onEditBtnClick(this, course));
        }
    }

    public interface OnClickListener {
        void onDeleteBtnClick(Dialog dialog, Course course);

        void onEditBtnClick(Dialog dialog, Course course);
    }

}
