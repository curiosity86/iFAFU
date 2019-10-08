package cn.ifafu.ifafu.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.TreeSet;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.util.DensityUtils;

public class WeekItemAdapter extends RecyclerView.Adapter<WeekItemAdapter.VH> {

    private Context context;
    private TreeSet<Integer> weekList;
    private OnItemClickListener listener;

    public boolean EDIT_MODE = false;

    public WeekItemAdapter(Context context) {
        this.context = context;
        weekList = new TreeSet<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) DensityUtils.dp2px(context, 64));
        int px1 = (int) DensityUtils.dp2px(context, 1);
        params.setMargins(px1 >> 1, px1, px1 >> 1, 0);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(Color.WHITE);
        return new VH(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.textView.setText(String.valueOf(position + 1));
        if (weekList.contains(position + 1)) {
            holder.textView.setBackgroundResource(R.color.ifafu_blue);
        } else {
            holder.textView.setBackgroundResource(R.color.light_gray);
        }
        holder.textView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
            if (!EDIT_MODE) return;
            if (weekList.contains(position + 1)) {
                weekList.remove(position + 1);
                holder.textView.setBackgroundResource(R.color.light_gray);
            } else {
                weekList.add(position + 1);
                holder.textView.setBackgroundResource(R.color.cyan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    public TreeSet<Integer> getWeekList() {
        return weekList;
    }

    public void setWeekList(TreeSet<Integer> weekList) {
        this.weekList = weekList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class VH extends RecyclerView.ViewHolder {

        TextView textView = (TextView) itemView;

        VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
