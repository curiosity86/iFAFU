package cn.ifafu.ifafu.ui.syllabus_item

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.databinding.SyllabusItemActivityBinding
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import cn.ifafu.ifafu.view.adapter.WeekItemAdapter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.syllabus_item_activity.*

class SyllabusItemActivity : BaseActivity<SyllabusItemActivityBinding, SyllabusItemViewModel>(), View.OnClickListener {

    private val timeOPV: OptionsPickerView<String> by lazy {
        OptionsPickerBuilder(this) { options1, options2, options3, _ ->
            course.run {
                weekday = options1 + 1
                beginNode = options2 + 1
                nodeCnt = options3 + 1
            }
            mBinding.time = "${weeks[options1]}  第${options2 + 1}节 ~ 第${options2 + options3 + 1}节"
        }
                .setOutSideCancelable(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择时间")
                .setTitleColor(Color.parseColor("#157efb"))
                .setTitleSize(13)
                .build<String>()
    }

    private val weeks = listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
    private lateinit var node1: List<List<String>>
    private lateinit var node2: List<List<List<String>>>
    private lateinit var course: Course

    private val mWeekAdapter: WeekItemAdapter by lazy { WeekItemAdapter(this) }

    override fun getLayoutId(): Int {
        return R.layout.syllabus_item_activity
    }

    override fun getViewModel(): SyllabusItemViewModel? {
        return VMProvider(this)[SyllabusItemViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_exam_item)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        //通过点击添加按钮开启页面，则直接进入编辑模式
        val comeFrom = intent.getIntExtra("come_from", -1)
        mBinding.layoutManager = GridLayoutManager(this, 6, RecyclerView.VERTICAL, false)
        mBinding.adapter = mWeekAdapter
        mViewModel.setting.observe(this, Observer {
            val node1t: MutableList<String> = ArrayList()
            val node2t: MutableList<MutableList<String>> = ArrayList()
            for (i in 1..it.totalNode) {
                node1t.add("第" + i + "节")
                val node2tt = ArrayList<String>()
                for (j in i..it.totalNode) {
                    node2tt.add("第" + j + "节")
                }
                node2t.add(node2tt)
            }
            val node1: MutableList<MutableList<String>> = ArrayList()
            val node2: MutableList<MutableList<MutableList<String>>> = ArrayList()
            for (i in 1..7) {
                node1.add(node1t)
            }
            for (i in 1..node1.size) {
                node2.add(node2t)
            }
            this.node1 = node1
            this.node2 = node2
            timeOPV.setPicker(weeks, node1, node2)
        })
        mViewModel.course.observe(this, Observer {
            if (comeFrom != SyllabusActivity.BUTTON_ADD) {
                mBinding.time = "${weeks[it.weekday - 1]}  第${it.beginNode}节 ~ 第${it.nodeCnt + it.beginNode - 1}节"
                timeOPV.setSelectOptions(it.weekday - 1, it.beginNode - 1, it.nodeCnt - 1)
                mBinding.course = it
                mWeekAdapter.weekList = it.weekSet
                mWeekAdapter.notifyDataSetChanged()
            }
            course = it
        })
        mViewModel.editMode.observe(this, Observer {
            mWeekAdapter.EDIT_MODE = it
            mBinding.editMode = it
        })
        mViewModel.resultCode.observe(this, Observer {
            setResult(it)
        })
        mViewModel.init(intent.getLongExtra("course_id", -1))
        et_course_time.setOnClickListener(this)
        btn_edit.setOnClickListener(this)
        btn_delete.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.et_course_time -> timeOPV.show()
            R.id.btn_edit -> mBinding.editMode = true
            R.id.btn_delete -> mViewModel.delete()
            R.id.btn_ok -> {
                mViewModel.save(course.apply {
                    weekSet = mWeekAdapter.weekList
                    name = et_course_name.text.toString()
                    address = et_course_address.text.toString()
                    teacher = et_course_teacher.text.toString()
                })
            }
        }
    }

}
