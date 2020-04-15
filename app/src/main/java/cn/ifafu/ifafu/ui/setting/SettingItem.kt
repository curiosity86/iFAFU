package cn.ifafu.ifafu.ui.setting

import android.widget.ImageView
import androidx.annotation.ColorInt
import com.chad.library.adapter.base.entity.MultiItemEntity

sealed class SettingItem : MultiItemEntity {

    class CheckBox(
            val title: String,
            val tip: String,
            var checked: Boolean,
            val onCheck: (checked: Boolean) -> Unit
    ) : SettingItem() {
        override val itemType: Int
            get() = CHECK_BOX
    }

    class SeekBar(
            val title: String,
            var value: Int,
            val unit: String,
            val minValue: Int,
            val maxValue: Int,
            val onChange: (progress: Int) -> Unit
    ) : SettingItem() {
        override val itemType: Int
            get() = SEEK_BAR
    }

    class Text(
            val title: String,
            val subtitle: String?,
            val onClick: () -> Unit,
            val onLongClick: (() -> Unit)? = null
    ) : SettingItem() {
        override val itemType: Int
            get() = TEXT
    }

    class Color(
            val title: String,
            val subtitle: String?,
            @ColorInt
            val color: Int,
            val onClick: (ImageView) -> Unit
    ) : SettingItem() {
        override val itemType: Int
            get() = COLOR
    }

    companion object {
        const val TEXT = 1
        const val SEEK_BAR = 2
        const val COLOR = 3
        const val CHECK_BOX = 4
    }
}
