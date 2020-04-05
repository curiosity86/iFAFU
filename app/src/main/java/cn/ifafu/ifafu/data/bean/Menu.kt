package cn.ifafu.ifafu.data.bean

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

class Menu(
        @IdRes
        val id: Int = 0,
        @DrawableRes
        val icon: Int, //图标
        val title: String,  //标题
        val activityClass: Class<out Activity>? = null
)
