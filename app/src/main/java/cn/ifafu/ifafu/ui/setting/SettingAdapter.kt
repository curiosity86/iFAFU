package cn.ifafu.ifafu.ui.setting

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.ui.view.custom.SmoothCheckBox
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class SettingAdapter : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>() {

    init {
        addItemType(SettingItem.TEXT, R.layout.item_setting_text)
        addItemType(SettingItem.COLOR, R.layout.item_setting_color)
        addItemType(SettingItem.SEEK_BAR, R.layout.item_setting_seekbar)
        addItemType(SettingItem.CHECK_BOX, R.layout.item_setting_checkbox)
    }

    override fun convert(holder: BaseViewHolder, item: MultiItemEntity) {
        when (item) {
            is SettingItem.CheckBox -> {
                holder.setText(R.id.tv_title, item.title)
                val subtitle = holder.getView<TextView>(R.id.tv_tip)
                if (item.tip.isNotBlank()) {
                    subtitle.visibility = View.VISIBLE
                    subtitle.text = item.tip
                } else {
                    subtitle.visibility = View.GONE
                }
                val checkBox = holder.getView<SmoothCheckBox>(R.id.checkbox)
                checkBox.setChecked(item.checked, false)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    item.onCheck(isChecked)
                }
                holder.itemView.setOnClickListener {
                    checkBox.isChecked = !checkBox.isChecked
                    checkBox.setChecked(checkBox.isChecked, true)
                    item.onCheck(checkBox.isChecked)
                }
            }
            is SettingItem.SeekBar -> {
                holder.setText(R.id.tv_title, item.title)
                holder.setText(R.id.tv_subtitle, "${item.value} ${item.unit}")
                val seekBar = holder.getView<SeekBar>(R.id.seekBar)
                seekBar.progress = item.value
                seekBar.max = item.maxValue - item.minValue
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        item.onChange(item.minValue + progress)
                        holder.setText(R.id.tv_subtitle, "${item.minValue + progress} ${item.unit}")
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }
            is SettingItem.Color -> {
                holder.setText(R.id.tv_title, item.title)
                holder.setText(R.id.tv_subtitle, item.subtitle)
                val imageView = holder.getView<ImageView>(R.id.iv_color)
                val grad = imageView.background as? GradientDrawable
                grad?.setColor(item.color)
                imageView.setOnClickListener {
                    item.onClick(imageView)
                }
            }
            is SettingItem.Text -> {
                holder.setText(R.id.tv_title, item.title)
                holder.setText(R.id.tv_subtitle, item.subtitle)
                holder.itemView.setOnClickListener {
                    item.onClick()
                }
                if (item.onLongClick != null) {
                    holder.itemView.setOnLongClickListener {
                        item.onLongClick.invoke()
                        true
                    }
                }
            }
        }
    }


}
