package cn.ifafu.ifafu.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.ScoreFilter

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

    fun getIES(scoreList: List<Score>, scoreFilter: ScoreFilter): Float {
        if (scoreList.isEmpty()) {
            return 0f
        }
        var totalScore = 0f
        var totalCredit = 0f
        var totalMinus = 0f
        for (score in scoreList) {
            if (score.id !in scoreFilter.filterList && score.realScore != Score.FREE_COURSE) {
                val realScore = score.realScore
                totalScore += (realScore * score.credit)
                totalCredit += score.credit
                if (realScore < 60) {
                    totalMinus -= score.credit
                }
            }
        }
        var result = totalScore / totalCredit - totalMinus
        if (result.isNaN()) {
            result = 0F
        }
        return result
    }

    fun getLocalVersionName(context: Context): String {
        var localVersion = ""
        try {
            val packageInfo = context.applicationContext
                    .packageManager
                    .getPackageInfo(context.packageName, 0)
            localVersion = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return localVersion
    }

    fun getLocalVersionCode(context: Context): Int {
        return try {
            val packageInfo = context.applicationContext
                    .packageManager
                    .getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            9999
        }
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

    fun hideSoftKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }
}