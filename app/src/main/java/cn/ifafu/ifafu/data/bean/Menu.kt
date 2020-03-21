package cn.ifafu.ifafu.data.bean

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

data class Menu(
        @DrawableRes val icon: Int, //图标
        val title: String,  //标题
        val activityClass: Class<out Activity>
)
