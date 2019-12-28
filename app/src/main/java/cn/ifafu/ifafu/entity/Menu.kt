package cn.ifafu.ifafu.entity

import android.content.Intent
import android.graphics.drawable.Drawable

data class Menu(
        val icon: Drawable, //图标
        val title: String,  //标题
        val intent: Intent
)