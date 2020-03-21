package cn.ifafu.ifafu.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.view.adapter.MenuAdapter.MenuViewHolder

class MenuAdapter(context: Context, menuList: List<Menu>) : RecyclerView.Adapter<MenuViewHolder>() {
    private var mMenuList: List<Menu> = menuList
    private var mContext: Context = context
    private var mItemClickListener: ((View?, Menu) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.main_menu_recycle_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = mMenuList[position]
        holder.titleTV.text = menu.title
        holder.iconIV.setImageResource(menu.icon)
        holder.itemView.setOnClickListener { v: View? ->
            mItemClickListener?.invoke(v, menu)
        }
    }

    fun setOnMenuClickListener(listener: (View?, Menu) -> Unit) {
        mItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return mMenuList.size
    }

    fun setMenuList(menus: List<Menu>) {
        mMenuList = menus
    }

    interface OnClickListener {
        fun onClick(v: View?, menu: Menu?)
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.tv_title)
        val iconIV: ImageView = itemView.findViewById(R.id.iv_icon)
    }
}