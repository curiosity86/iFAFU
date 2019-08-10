package cn.ifafu.ifafu.data

import android.app.Activity
import android.graphics.drawable.Drawable

class Menu(
        var icon: Drawable,
        var title: String,
        var activityClass: Class<out Activity>
)
