package cn.ifafu.ifafu.mvp.main.main1

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.entity.Holiday
import cn.ifafu.ifafu.data.entity.Menu
import cn.ifafu.ifafu.mvp.activity.CommentActivity
import cn.ifafu.ifafu.mvp.elec_splash.ElecSplashActivity
import cn.ifafu.ifafu.mvp.exam.ExamActivity
import cn.ifafu.ifafu.mvp.main.BaseMainModel
import cn.ifafu.ifafu.mvp.score.ScoreActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel
import cn.ifafu.ifafu.mvp.web.WebActivity
import io.reactivex.Observable

class Main1Model(context: Context) : BaseMainModel(context), Main1Contract.Model {

    override fun getMenus(): Observable<List<Menu>> {
        return Observable.fromCallable {
            listOf(
                    Menu(drawable(R.drawable.tab_syllabus), "课程表", intent(SyllabusActivity::class.java)),
                    Menu(drawable(R.drawable.tab_exam), "考试计划", intent(ExamActivity::class.java)),
                    Menu(drawable(R.drawable.tab_score), "成绩查询", intent(ScoreActivity::class.java)),
                    Menu(drawable(R.drawable.tab_web), "网页模式", intent(WebActivity::class.java)),
                    Menu(drawable(R.drawable.tab_electricity), "电费查询", intent(ElecSplashActivity::class.java)),
                    Menu(drawable(R.drawable.tab_repair), "报修服务", intent(WebActivity::class.java).apply {
                        putExtra("title", "报修服务")
                        putExtra("url", Constant.REPAIR_URL)
                    }),
                    Menu(drawable(R.drawable.tab_comment), "一键评教", intent(CommentActivity::class.java))
            )
        }
    }

    private fun drawable(@DrawableRes id: Int): Drawable = mContext.getDrawable(id)!!

    private fun intent(cls: Class<*>): Intent = Intent(mContext, cls)

    override fun getSchoolIcon(): Drawable {
        return when (repository.loginUser.schoolCode) {
            School.FAFU -> drawable(R.drawable.icon_fafu_b_white)
            School.FAFU_JS -> drawable(R.drawable.icon_fafu_js_white)
            else -> drawable(R.mipmap.ic_launcher_round)
        }
    }

    override fun getHoliday(): List<Holiday> {
        return SyllabusModel(mContext).holidays
    }

    override fun clearAllDate() {
        repository.clearAllData()
    }
}