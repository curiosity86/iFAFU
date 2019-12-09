package cn.ifafu.ifafu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Menu;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<Menu> mMenuList;
    private Context mContext;
    private OnClickListener mItemClickListener;

    public MenuAdapter(Context context) {
        mContext = context;
        mMenuList = new ArrayList<>();
    }

    public MenuAdapter(Context context, List<Menu> menuList) {
        mMenuList = menuList;
        mContext = context;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_menu_recycle_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        final Menu menu = mMenuList.get(position);
        holder.titleTV.setText(menu.getTitle());
        holder.iconIV.setImageDrawable(menu.getIcon());
        holder.itemView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onClick(v, menu);
            }
        });
    }

    public void setOnMenuClickListener(OnClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

    public void setMenuList(List<Menu> menus) {
        mMenuList = menus;
    }

    public interface OnClickListener {
        void onClick(View v, Menu menu);
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;

        ImageView iconIV;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.tv_title);
            iconIV = itemView.findViewById(R.id.iv_icon);
        }
    }
}
