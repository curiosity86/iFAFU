@file:JvmName("Converter")

package cn.ifafu.ifafu.util

import android.content.Context
import android.graphics.drawable.Drawable
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.constant.School
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.User

fun semesterToString(semester: Semester?): String {
    return when {
        semester == null ->
            return ""
        semester.termStr == "全部" && semester.yearStr == "全部" ->
            "全部"
        semester.termStr == "全部" ->
            "${semester.yearStr}学年全部"
        else ->
            "${semester.yearStr}学年第${semester.termStr}学期"
    }
}

fun schoolToIconWhite(context: Context, @School school: String?): Drawable? {
    return context.getDrawable(when (school) {
        User.FAFU -> R.drawable.fafu_bb_icon_white
        User.FAFU_JS -> R.drawable.fafu_js_icon_white
        else -> R.drawable.icon_ifafu_round
    })
}

fun schoolToString(@School school: String?): String? {
    return when (school) {
        User.FAFU -> "福建农林大学"
        User.FAFU_JS -> "福建农林大学金山学院"
        else -> null
    }
}
