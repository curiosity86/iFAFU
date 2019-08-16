package cn.ifafu.ifafu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Exam;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private Context mContext;
    private List<Exam> mExamList;

    public ExamAdapter(Context context, List<Exam> data) {
        mContext = context;
        mExamList = data;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_exam, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = mExamList.get(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        holder.tvExamDate.setText(format.format(new Date(exam.getStartTime())));
        holder.tvExamName.setText(exam.getName());
        holder.tvExamAddress.setText(String.format("%s   %s号", exam.getAddress(), exam.getSeatNumber()));
        if (exam.getEndTime() < System.currentTimeMillis()) {
            holder.tvExamLast.setText(R.string.exam_over);
        }
    }

    @Override
    public int getItemCount() {
        return mExamList.size();
    }

    public void setExamData(List<Exam> data) {
        mExamList = data;
        final long now = System.currentTimeMillis();
        Collections.sort(data, (o1, o2) -> {
            if (o1.getEndTime() < now && o2.getEndTime() < now) {
                return Long.compare(o2.getEndTime(), o1.getEndTime());
            } else if (o1.getEndTime() < now) {
                return -1;
            } else if (o2.getEndTime() < now) {
                return 1;
            } else {
                return Long.compare(o1.getEndTime(), o2.getEndTime());
            }
        });
        notifyDataSetChanged();
    }

    class ExamViewHolder extends RecyclerView.ViewHolder {

        TextView tvExamName;
        TextView tvExamTime;
        TextView tvExamDate;
        TextView tvExamAddress;
        TextView tvExamLast;

        ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamName = itemView.findViewById(R.id.tv_exam_name);
            tvExamTime = itemView.findViewById(R.id.tv_exam_time);
            tvExamDate = itemView.findViewById(R.id.tv_exam_date);
            tvExamAddress = itemView.findViewById(R.id.tv_exam_address);
            tvExamLast = itemView.findViewById(R.id.tv_exam_last);
        }
    }
}
