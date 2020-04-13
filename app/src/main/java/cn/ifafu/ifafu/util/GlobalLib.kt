package cn.ifafu.ifafu.util

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import cn.ifafu.ifafu.data.entity.Score

object GlobalLib {
    fun trimZero(s: String): String {
        var s = s
        if (s.indexOf(".") > 0) { // 去掉多余的0
            s = s.replace("0+?$".toRegex(), "")
            // 如最后一位是.则去掉
            s = s.replace("[.]$".toRegex(), "")
        }
        return s
    }

    fun formatFloat(num: Float, digit: Int): String {
        return trimZero(String.format("%." + digit + "f", num))
    }

    @JvmStatic
    fun getActivityFromView(view: View?): Activity? {
        if (null != view) {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
        }
        return null
    }

    fun List<Score>.calcIES(): Float {
        // 过滤 不计入智育分的成绩 和 免修成绩
        val scores = this
                .filter { it.isIESItem && it.realScore != Score.FREE_COURSE }
        if (scores.isEmpty()) {
            return 0f
        }
        var totalScore = 0f
        var totalCredit = 0f
        var totalMinus = 0f
        for (score in scores) {
            val realScore = score.realScore
            totalScore += (realScore * score.credit)
            totalCredit += score.credit
            if (realScore < 60) {
                totalMinus += score.credit
            }
        }
        var result = totalScore / totalCredit - totalMinus
        if (result.isNaN()) {
            result = 0F
        }
        return result
    }
}