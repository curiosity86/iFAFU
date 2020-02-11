package cn.ifafu.ifafu.data.entity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable

data class Menu(
        val icon: Drawable, //图标
        val title: String,  //标题
        val activityClass: Class<out Activity>
)
