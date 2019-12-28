package cn.ifafu.ifafu.mvp.syllabus_item

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.view.adapter.WeekItemAdapter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.syllabus_info_activity.*
import java.util.*

class SyllabusItemActivity : BaseActivity<SyllabusItemContract.Presenter>(), SyllabusItemContract.View, View.OnClickListener {

    private lateinit var timeOPV: OptionsPickerView<String>

    private lateinit var weekAdapter: WeekItemAdapter

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.syllabus_info_activity
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_exam_item)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()

        mPresenter = SyllabusItemPresenter(this)

        weekAdapter = WeekItemAdapter(this)
        rv_course_weeks.layoutManager = GridLayoutManager(this, 6, RecyclerView.VERTICAL, false)
        rv_course_weeks.adapter = weekAdapter

        timeOPV = OptionsPickerBuilder(this) { options1, options2, options3, v ->
            val op3: Int = if (options2 > options3) options2 else options3
            timeOPV.setSelectOptions(options1, options2, op3)
            mPresenter.onTimeSelect(options1, options2, op3)
        }
                .setOptionsSelectChangeListener { options1, options2, options3 ->
                    if (options2 > options3) {
                        timeOPV.setSelectOptions(options1, options2, options2)
                    }
                }
                .setOutSideCancelable(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择时间")
                .setTitleColor(Color.parseColor("#157efb"))
                .setTitleSize(13)
                .build()

        et_course_time.setOnClickListener(this)
        btn_edit.setOnClickListener(this)
        btn_delete.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
    }

    private fun setTimeText(text: String) {
        tv_course_time.text = text
        et_course_time.text = text
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.et_course_time -> timeOPV.show()
            R.id.btn_edit -> mPresenter.onEdit()
            R.id.btn_delete -> mPresenter.onDelete()
            R.id.btn_ok -> mPresenter.onSave()
        }
    }

    override fun setTimeOPVOptions(op1: List<String>, op2: List<String>, op3: List<String>) {
        timeOPV.setNPicker(op1, op2, op3)
    }

    override fun getWeekData(): TreeSet<Int> {
        return weekAdapter.weekList
    }

    override fun setWeekData(weekData: TreeSet<Int>) {
        weekAdapter.weekList = weekData
        weekAdapter.notifyDataSetChanged()
    }

    override fun setTimeOPVSelect(op1: Int, op2: Int, op3: Int, text: String) {
        timeOPV.setSelectOptions(op1, op2, op3)
        setTimeText(text)
    }

    override fun isEditMode(isEditMode: Boolean) {
        val v1: Int
        val v2: Int
        weekAdapter.EDIT_MODE = isEditMode
        if (isEditMode) {
            v1 = View.VISIBLE
            v2 = View.GONE
        } else {
            v1 = View.GONE
            v2 = View.VISIBLE
        }
        btn_ok.visibility = v1
        et_course_time.visibility = v1
        et_course_name.visibility = v1
        et_course_address.visibility = v1
        et_course_teacher.visibility = v1
        btn_delete.visibility = v2
        btn_edit.visibility = v2
        tv_course_address.visibility = v2
        tv_course_name.visibility = v2
        tv_course_teacher.visibility = v2
        tv_course_time.visibility = v2
    }

    override fun getNameText(): String {
        return et_course_name.text.toString()
    }

    override fun getAddressText(): String {
        return et_course_address.text.toString()
    }

    override fun getTeacherText(): String {
        return et_course_teacher.text.toString()
    }

    override fun setNameText(name: String) {
        tv_course_name.text = name
        et_course_name.setText(name)
    }

    override fun setAddressText(address: String) {
        tv_course_address.text = address
        et_course_address.setText(address)
    }

    override fun setTeacherText(teacher: String) {
        tv_course_teacher.text = teacher
        et_course_teacher.setText(teacher)
    }
}
