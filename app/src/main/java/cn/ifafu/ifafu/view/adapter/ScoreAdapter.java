package cn.ifafu.ifafu.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.util.GlobalLib;

public class ScoreAdapter  extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private Context mContext;
    private List<Score> mScoreList;

    private View.OnClickListener itemClickListener;

    public ScoreAdapter(Context context, List<Score> scores) {
        mContext = context;
        mScoreList = scores;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = mScoreList.get(position);
        holder.tvName.setText(score.getName());
        holder.tvScore.setText(GlobalLib.trimZero(String.valueOf(score.getScore())));
        holder.itemView.setOnClickListener(itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mScoreList.size();
    }

    public void setScoreData(List<Score> scores) {
        mScoreList = scores;
    }

    public void setItemClickListener(View.OnClickListener listener) {
        itemClickListener = listener;
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvScore;

        ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_score_name);
            tvScore = itemView.findViewById(R.id.tv_score_score);
        }
    }
}
