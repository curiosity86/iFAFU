package cn.ifafu.ifafu.mvp.syllabus_item

import android.app.Activity
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.entity.Course
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

internal class SyllabusItemPresenter(view: SyllabusItemContract.View) : BasePresenter<SyllabusItemContract.View, SyllabusItemContract.Model>(view, SyllabusItemModel(view.context)), SyllabusItemContract.Presenter {

    private var come_from: Int = 0

    private var resultCode = Activity.RESULT_CANCELED

    private var course: Course? = null

    private val weekdays = listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
    private val nodes = ArrayList<String>()

    override fun onCreate() {
        GlobalScope.launch(Dispatchers.IO) {

            val setting = mModel.getSyllabusSetting()
            for (i in 1..setting.nodeLength) {
                nodes.add("第" + i + "节")
            }
            launch(Dispatchers.Main) {
                mView.setTimeOPVOptions(weekdays, nodes, nodes)
            }

            val intent = mView.activity.intent
            // 1 表示通过添加按钮打开Activity
            come_from = intent.getIntExtra("come_from", -1)
            if (come_from == SyllabusActivity.BUTTON_ADD) {
                launch(Dispatchers.Main) {
                    mView.isEditMode(true)
                }
            }
            // 获取跳转课程id
            val id = intent.getLongExtra("course_id", -1L)
            if (id != -1L) {
                course = mModel.getCourseById(id)
                if (course != null) {
                    launch(Dispatchers.Main) {
                        resetView(course!!)
                    }
                }
            }
            if (course == null) {
                course = Course()
            }
        }
    }

    private fun resetView(course: Course) {
        mView.setNameText(
                if (course.name.isNullOrEmpty()) "无"
                else course.name
        )
        mView.setTeacherText(
                if (course.teacher.isNullOrEmpty()) "无"
                else course.teacher
        )
        mView.setAddressText(
                if (course.address.isNullOrEmpty()) "无"
                else course.address
        )
        mView.setWeekData(course.weekSet)
        onTimeSelect(course.weekday - 1, course.beginNode - 1,
                course.beginNode + course.nodeCnt - 2)
    }

    override fun onSave() {
        mCompDisposable.add(Observable
                .fromCallable {
                    val name = mView.getNameText()
                    if (name.isNullOrEmpty()) {
                        return@fromCallable R.string.input_course_name
                    }
                    if (course!!.nodeCnt <= 0) {
                        return@fromCallable R.string.select_course_time
                    }
                    if (mView.getWeekData().isEmpty()) {
                        return@fromCallable R.string.select_course_week
                    }
                    val address = mView.getAddressText()
                    val teacher = mView.getTeacherText()
                    course!!.name = name
                    course!!.teacher = teacher ?: ""
                    course!!.address = address ?: ""
                    course!!.weekSet = mView.getWeekData()
                    course!!.local = true
                    mModel.save(course!!)
                    resultCode = Activity.RESULT_OK
                    R.string.save_successful
                }
                .compose(RxUtils.ioToMain())
                .subscribe({ stringRes ->
                    mView.activity.setResult(resultCode)
                    mView.showMessage(stringRes!!)
                    if (come_from == SyllabusActivity.BUTTON_ADD) {
                        if (stringRes == R.string.save_successful) {
                            mView.killSelf()
                        }
                    } else {
                        resetView(course!!)
                        mView.isEditMode(false)
                    }
                }, { this.onError(it) })
        )
    }

    override fun onEdit() {
        mView.isEditMode(true)
    }

    override fun onDelete() {

        GlobalScope.launch(Dispatchers.IO) {
            mModel.delete(course!!)
            launch(Dispatchers.Main) {
                mView.showMessage(R.string.delete_successful)
                mView.activity.setResult(Activity.RESULT_OK)
                mView.killSelf()
            }
        }
    }

    override fun onTimeSelect(options1: Int, options2: Int, options3: Int) {
        course!!.weekday = options1 + 1
        course!!.beginNode = options2 + 1
        course!!.nodeCnt = options3 - options2 + 1
        val text = String.format("%s %s - %s", weekdays[options1], nodes[options2], nodes[options3])
        mView.setTimeOPVSelect(options1, options2, options3, text)
    }
}
