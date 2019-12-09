package cn.ifafu.ifafu.mvp.main.main_old

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.entity.NextCourse
import cn.ifafu.ifafu.data.entity.NextExam
import cn.ifafu.ifafu.mvp.activity.AboutActivity
import cn.ifafu.ifafu.mvp.comment.CommentActivity
import cn.ifafu.ifafu.mvp.elec_main.ElecMainActivity
import cn.ifafu.ifafu.mvp.exam_list.ExamActivity
import cn.ifafu.ifafu.mvp.main.BaseMainFragment
import cn.ifafu.ifafu.mvp.score_list.ScoreListActivity
import cn.ifafu.ifafu.mvp.setting.SettingActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.mvp.web.WebActivity
import cn.ifafu.ifafu.util.ColorUtils
import cn.ifafu.ifafu.view.custom.LeftMenu
import cn.ifafu.ifafu.view.listener.ScrollDrawerListener
import cn.ifafu.ifafu.view.listener.TabClickListener
import kotlinx.android.synthetic.main.main_old_fragment.*
import kotlinx.android.synthetic.main.main_old_include.*
import kotlinx.android.synthetic.main.main_old_menu_include.*

class Main2Fragment : BaseMainFragment<Main2Contract.Presenter>(),
        Main2Contract.View,
        TabClickListener,
        View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.main_old_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        mPresenter = Main2Presenter(this)
        setOnlineStatus(true)
        tv_name.setOnClickListener(this)
        layout_syllabus.setOnClickListener(this)
        layout_exam.setOnClickListener(this)
        layout_score.setOnClickListener(this)
        layout_drawer.addDrawerListener(ScrollDrawerListener(nav_left_menu, layout_content))
    }

    override fun setNameText(name: String) {
        tv_name_big.text = name
        tv_name.text = name.substring(1)
        tv_left_name.text = name.substring(1)
    }

    override fun setAccountText(account: String) {
        tv_account.text = account
    }

    override fun makeLeftMenu(data: Map<String, List<Pair<String, Int>>>) {
        LeftMenu(layout_left_menu)
                .make(data)
                .setTabClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_name -> layout_drawer.openDrawer(GravityCompat.START)
            R.id.layout_exam -> startActivity(Intent(context, ExamActivity::class.java))
            R.id.layout_syllabus -> startActivity(Intent(context, SyllabusActivity::class.java))
            R.id.layout_score -> startActivity(Intent(context, ScoreListActivity::class.java))
        }
    }

    override fun setOnlineStatus(online: Boolean) {
        if (online) {
            tv_online.setTextColor(ColorUtils.getColor(context, R.color.green))
            tv_online.text = "● 在线"
            tv_subtitle.text = "在线"
        } else {
            tv_online.setTextColor(ColorUtils.getColor(context, R.color.red_2))
            tv_online.text = "● 离线"
            tv_subtitle.text = "离线"
        }
    }

    override fun setYearTermTitle(title: String) {
        layout_syllabus.findViewById<TextView>(R.id.tv_title).text = title + "课表"
        layout_exam.findViewById<TextView>(R.id.tv_title).text = title + "学生考试"
        layout_score.findViewById<TextView>(R.id.tv_title).text = title + "学习成绩"
    }

    override fun onTabClick(tabName: String) {
        when (tabName) {
            "成绩查询" -> startActivity(Intent(context, ScoreListActivity::class.java))
            "学生考试查询" -> startActivity(Intent(context, ExamActivity::class.java))
            "我的课表" -> startActivity(Intent(context, SyllabusActivity::class.java))
            "网页模式" -> startActivity(Intent(context, WebActivity::class.java))
            "电费查询" -> startActivity(Intent(context, ElecMainActivity::class.java))
            "报修服务" -> {
                startActivity(Intent(context, WebActivity::class.java).apply {
                    putExtra("title", "报修服务")
                    putExtra("url", Constant.REPAIR_URL)
                })
            }
            "一键评教" -> startActivity(Intent(context, CommentActivity::class.java))
            "账号管理" -> mPresenter.checkout()
            "软件设置" -> startActivityForResult(Intent(context, SettingActivity::class.java), Constant.ACTIVITY_SETTING)
            "检查更新" -> mPresenter.updateApp()
            "关于iFAFU" -> startActivity(Intent(context, AboutActivity::class.java))
        }
        layout_drawer.closeDrawer(GravityCompat.START)
    }

    override fun setWeatherText(text: String) {
        layout_syllabus.findViewById<TextView>(R.id.tv_weather).text = text
    }

    override fun onStart() {
        super.onStart()
        mPresenter.updateNextCourse()
        mPresenter.updateExamInfo()
        mPresenter.updateScoreInfo()
    }

    @SuppressLint("SetTextI18n")
    override fun setNextCourse(nextCourse: NextCourse) {
        layout_syllabus.findViewById<TextView>(R.id.tv_week_time).text = nextCourse.dateText
        when (nextCourse.result) {
            NextCourse.HAS_NEXT_COURSE, NextCourse.IN_COURSE -> {
                layout_syllabus.findViewById<TextView>(R.id.tv_null).visibility = View.GONE
                layout_syllabus.findViewById<LinearLayout>(R.id.layout_info).visibility = View.VISIBLE
                layout_syllabus.findViewById<TextView>(R.id.tv_next).text = nextCourse.title + nextCourse.name
                layout_syllabus.findViewById<TextView>(R.id.tv_location).text = nextCourse.address
                layout_syllabus.findViewById<TextView>(R.id.tv_time).text =
                        "第${nextCourse.node}节 ${nextCourse.timeText}"
                layout_syllabus.findViewById<TextView>(R.id.tv_last).text = nextCourse.lastText
                layout_syllabus.findViewById<TextView>(R.id.tv_total).text =
                        "今日:第${nextCourse.node}/${nextCourse.totalNode}节"
                layout_syllabus.findViewById<TextView>(R.id.tv_status).run {
                    if (nextCourse.result == NextCourse.IN_COURSE) {
                        text = "上课中"
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_blue, 0, 0, 0)
                    } else {
                        text = "未上课"
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_red, 0, 0, 0)
                    }
                }
            }
            else -> {
                layout_syllabus.findViewById<TextView>(R.id.tv_null).visibility = View.VISIBLE
                layout_syllabus.findViewById<LinearLayout>(R.id.layout_info).visibility = View.GONE
                layout_syllabus.findViewById<TextView>(R.id.tv_null).text = nextCourse.title
            }
        }
    }

    override fun setScoreText(text: String?) {
        if (text.isNullOrEmpty()) {
            layout_score.findViewById<TextView>(R.id.tv_null).visibility = View.VISIBLE
            layout_score.findViewById<TextView>(R.id.tv_score_detail).visibility = View.GONE
        } else {
            val tvScore = layout_score.findViewById<TextView>(R.id.tv_score_detail)
            layout_score.findViewById<TextView>(R.id.tv_null).visibility = View.GONE
            tvScore.visibility = View.VISIBLE
            tvScore.text = text
        }
    }

    override fun setExamData(data: List<NextExam>) {
        if (data.isEmpty()) {
            layout_exam.findViewById<TextView>(R.id.tv_null).visibility = View.VISIBLE
            layout_exam.findViewById<LinearLayout>(R.id.layout_exam1).visibility = View.GONE
            layout_exam.findViewById<LinearLayout>(R.id.layout_exam2).visibility = View.GONE
        } else {
            layout_exam.findViewById<TextView>(R.id.tv_null).visibility = View.GONE
            layout_exam.findViewById<LinearLayout>(R.id.layout_exam1).visibility = View.VISIBLE
            fillExamInfo(layout_exam.findViewById(R.id.layout_exam1), data[0], 1)
            if (data.size >= 2) {
                layout_exam.findViewById<LinearLayout>(R.id.layout_exam2).visibility = View.VISIBLE
                fillExamInfo(layout_exam.findViewById(R.id.layout_exam2), data[1], 2)
            } else {
                layout_exam.findViewById<LinearLayout>(R.id.layout_exam2).visibility = View.GONE
            }
        }
    }

    private fun fillExamInfo(view: ViewGroup, exam: NextExam, index: Int) {
        view.findViewById<TextView>(R.id.tv_num).text = index.toString()
        view.findViewById<TextView>(R.id.tv_name).text = exam.name
        view.findViewById<TextView>(R.id.tv_time).text = exam.time
        if (exam.address.isBlank()) {
            view.findViewById<TextView>(R.id.tv_location).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.tv_location).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.tv_location).text = exam.address
        }
        if (exam.seatNum.isBlank()) {
            view.findViewById<TextView>(R.id.tv_seat).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.tv_seat).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.tv_seat).text = exam.seatNum
        }
        view.findViewById<TextView>(R.id.tv_last).text = exam.last.first
        view.findViewById<TextView>(R.id.tv_unit).text = exam.last.second
    }
}