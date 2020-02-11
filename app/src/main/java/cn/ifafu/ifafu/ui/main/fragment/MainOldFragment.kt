package cn.ifafu.ifafu.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.mvvm.BaseFragment
import cn.ifafu.ifafu.data.entity.NextCourse
import cn.ifafu.ifafu.data.entity.NextExam
import cn.ifafu.ifafu.databinding.MainOldFragmentBinding
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.elective.ElectiveActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.score_list.ScoreListActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import cn.ifafu.ifafu.ui.web.WebActivity
import cn.ifafu.ifafu.util.ColorUtils
import cn.ifafu.ifafu.view.custom.LeftMenu
import cn.ifafu.ifafu.view.listener.ScrollDrawerListener
import cn.ifafu.ifafu.view.listener.TabClickListener
import kotlinx.android.synthetic.main.main_old_fragment.*
import kotlinx.android.synthetic.main.main_old_menu_include.*

class MainOldFragment : BaseFragment<MainOldFragmentBinding, MainViewModel>(),
        TabClickListener,
        View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.main_old_fragment
    }

    override fun getViewModel(): MainViewModel {
        return VMProvider(requireActivity())[MainViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    override fun initFragment(savedInstanceState: Bundle?) {
        tv_name.setOnClickListener(this)
        layout_syllabus.setOnClickListener(this)
        layout_exam.setOnClickListener(this)
        layout_score.setOnClickListener(this)
        layout_drawer.addDrawerListener(ScrollDrawerListener(nav_left_menu, layout_content))
        mViewModel.inUseUser.observe(this, Observer {
            tv_name_big.text = it.name
            tv_name.text = it.name.substring(1)
            tv_left_name.text = it.name.substring(1)
            tv_account.text = it.account
        })
        mViewModel.nextCourse.observe(this, Observer {
            setNextCourse(it)
        })
        mViewModel.weather.observe(this, Observer {
            setWeatherText("${it.nowTemp}℃|${it.weather}")
        })
        mViewModel.scores.observe(this, Observer {
            setScoreText("已出${it.size}门成绩")
        })
        mViewModel.oldThemeMenu.observe(this, Observer {
            makeLeftMenu(it)
        })
        mViewModel.semesterTitle.observe(this, Observer {
            layout_syllabus.findViewById<TextView>(R.id.tv_title).text = it + "课表"
            layout_exam.findViewById<TextView>(R.id.tv_title).text = it + "学生考试"
            layout_score.findViewById<TextView>(R.id.tv_title).text = it + "学习成绩"
        })
        mViewModel.nextExam.observe(this, Observer {
            setExamData(it)
        })
        mViewModel.onlineStatus.observe(this, Observer {
            if (it) {
                tv_online.setTextColor(ColorUtils.getColor(context, R.color.green))
                tv_online.text = "● 在线"
                tv_subtitle.text = "在线"
            } else {
                tv_online.setTextColor(ColorUtils.getColor(context, R.color.red_2))
                tv_online.text = "● 离线"
                tv_subtitle.text = "离线"
            }
        })
        mViewModel.initFragmentData()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.updateExamInfo()
        mViewModel.updateScoreInfo()
        mViewModel.updateNextCourse()
        mViewModel.updateWeather()
    }

    private fun makeLeftMenu(data: List<Pair<String, List<Pair<String, Int>>>>) {
        LeftMenu(layout_left_menu)
                .make(data)
                .setTabClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_name -> layout_drawer.openDrawer(GravityCompat.START)
            R.id.layout_exam -> startActivity(Intent(activity, ExamListActivity::class.java))
            R.id.layout_syllabus -> startActivity(Intent(activity, SyllabusActivity::class.java))
            R.id.layout_score -> startActivity(Intent(activity, ScoreListActivity::class.java))
        }
    }

    override fun onTabClick(tabName: String) {
        when (tabName) {
            "成绩查询" -> startActivity(Intent(activity, ScoreListActivity::class.java))
            "学生考试查询" -> startActivity(Intent(activity, ExamListActivity::class.java))
            "我的课表" -> startActivity(Intent(activity, SyllabusActivity::class.java))
            "网页模式" -> startActivity(Intent(activity, WebActivity::class.java))
            "电费查询" -> startActivity(Intent(activity, ElectricityActivity::class.java))
            "报修服务" -> {
                startActivity(Intent(activity, WebActivity::class.java).apply {
                    putExtra("title", "报修服务")
                    putExtra("url", Constant.REPAIR_URL)
                })
            }
            "选修学分查询" -> startActivity(Intent(activity, ElectiveActivity::class.java))
//            "一键评教" -> startActivity(Intent(context, CommentActivity::class.java))
            "账号管理" -> mViewModel.switchAccount()
            "软件设置" -> requireActivity().startActivityForResult(Intent(context, SettingActivity::class.java), Constant.ACTIVITY_SETTING)
            "检查更新" -> mViewModel.upgradeApp()
            "关于iFAFU" -> startActivity(Intent(activity, AboutActivity::class.java))
        }
        layout_drawer.closeDrawer(GravityCompat.START)
    }

    private fun setWeatherText(text: String) {
        layout_syllabus.findViewById<TextView>(R.id.tv_weather).text = text
    }

    @SuppressLint("SetTextI18n")
    fun setNextCourse(nextCourse: NextCourse) {
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

    private fun setScoreText(text: String) {
        if (text.isEmpty()) {
            layout_score.findViewById<TextView>(R.id.tv_null).visibility = View.VISIBLE
            layout_score.findViewById<TextView>(R.id.tv_score_detail).visibility = View.GONE
        } else {
            val tvScore = layout_score.findViewById<TextView>(R.id.tv_score_detail)
            layout_score.findViewById<TextView>(R.id.tv_null).visibility = View.GONE
            tvScore.visibility = View.VISIBLE
            tvScore.text = text
        }
    }

    private fun setExamData(data: List<NextExam>) {
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