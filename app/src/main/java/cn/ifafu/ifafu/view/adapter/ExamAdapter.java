package cn.ifafu.ifafu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Exam;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private Context mContext;
    private List<Exam> mExamData;

    public ExamAdapter(Context context, List<Exam> data) {
        mContext = context;
        mExamData = data;
    }

    @NonNull
    @Override
    public ExamAdapter.ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_exam, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamAdapter.ExamViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mExamData.size();
    }

    public void setExamData(List<Exam> data) {
        mExamData = data;
        notifyDataSetChanged();
    }

    class ExamViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV;
        TextView dateTV;
        TextView timeTV;
        TextView roomTV;
        TextView seatTV;
        TextView lastTimeTV;
        TextView timeUnitTV;

        ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.tv_name);
            dateTV = itemView.findViewById(R.id.tv_date);
            timeTV = itemView.findViewById(R.id.tv_time);
            roomTV = itemView.findViewById(R.id.tv_room);
            seatTV = itemView.findViewById(R.id.tv_seat);
            lastTimeTV = itemView.findViewById(R.id.tv_last_time);
            timeUnitTV = itemView.findViewById(R.id.tv_time_unit);
        }
    }
}
