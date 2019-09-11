package cn.ifafu.ifafu.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.util.GlobalLib;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private Context mContext;
    private List<Score> mScoreList;

    private OnScoreClickListener mClickListener;

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
        final Score score = mScoreList.get(position);
        holder.tvName.setText(score.getName());
        float calcScore = score.getRealScore();
        if (calcScore == Score.FREE_COURSE) {
            holder.tvScore.setText("免修");
            holder.ivTip.setImageResource(R.drawable.ic_score_mian);
        } else {
            holder.tvScore.setText(GlobalLib.formatFloat(calcScore, 2));
            if (score.getName().contains("体育")) {
                holder.ivTip.setImageResource(R.drawable.ic_score_ti);
            } else if (score.getNature().contains("任意选修")) {
                holder.ivTip.setImageResource(R.drawable.ic_score_xuan);
            } else if (calcScore < 60) {
                holder.ivTip.setImageResource(R.drawable.ic_score_warm);
            } else {
                holder.ivTip.setImageDrawable(null);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClick(score);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mScoreList.size();
    }

    public void setScoreData(List<Score> scores) {
        mScoreList = scores;
    }

    public void setOnScoreClickListener(OnScoreClickListener listener) {
        mClickListener = listener;
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvScore;
        ImageView ivTip;

        ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_score_name);
            tvScore = itemView.findViewById(R.id.tv_score_score);
            ivTip = itemView.findViewById(R.id.iv_tip);
        }
    }

    public interface OnScoreClickListener {
        void onClick(Score score);
    }
}
