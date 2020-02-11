package cn.ifafu.ifafu.view.dialog

import android.content.Context
import android.graphics.Color
import cn.ifafu.ifafu.data.entity.Semester
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView

class SemesterOptionPickerBuilder(context: Context) : OptionsPickerBuilder(context, null) {

    private var semester: Semester? = null

    init {
        setCancelText("取消")
        setSubmitText("确定")
        setTitleText("请选择学年与学期")
        setTitleColor(Color.parseColor("#157efb"))
        setTitleSize(13)
    }

    fun setSemester(semester: Semester): SemesterOptionPickerBuilder {
        this.semester = semester
        setSelectOptions(semester.yearIndex, semester.termIndex)
        return this
    }

    fun setSelectListener(listener: (semester: Semester) -> Unit) {
        setOptionsSelectChangeListener { options1, options2, options3 ->
            semester?.run {
                yearIndex = options1
                termIndex = options2
                listener(this)
            }
        }
    }

    fun buildDialog(): OptionsPickerView<String> {
        val dialog = super.build<String>()
        semester?.run {
            dialog.setNPicker(yearList, termList, null)
        }
        return dialog
    }


}

