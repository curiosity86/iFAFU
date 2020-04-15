package cn.ifafu.ifafu.ui.view.adapter

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.databinding.ItemMainNewTabBinding
import cn.ifafu.ifafu.ui.view.listener.OnMenuItemClickListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class MenuAdapter(
        listener: OnMenuItemClickListener
) : BaseQuickAdapter<Menu, BaseDataBindingHolder<ItemMainNewTabBinding>>(R.layout.item_main_new_tab) {

    private var onMenuClickListener = listener
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: BaseDataBindingHolder<ItemMainNewTabBinding>, item: Menu) {
        holder.dataBinding?.run {
            menu = item
            setOnClick {
                onMenuClickListener.onMenuItemClick(item)
            }
        }
    }
}