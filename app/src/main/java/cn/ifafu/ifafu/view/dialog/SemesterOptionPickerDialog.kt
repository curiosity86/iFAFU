package cn.ifafu.ifafu.view.dialog

import android.content.Context
import android.graphics.Color
import cn.ifafu.ifafu.data.entity.Semester
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.configure.PickerOptions
import com.bigkoo.pickerview.view.OptionsPickerView

class SemesterOptionPickerDialog private constructor(
        pickerOptions: PickerOptions,
        private var semester: Semester)
    : OptionsPickerView<String>(pickerOptions) {

    fun setSemester(semester: Semester) {
        this.semester = semester
        setSelectOptions(semester.yearIndex, semester.termIndex)
        setNPicker(semester.yearList, semester.termList, null)
    }

    fun setSelectedListener(listener: (Semester) -> Unit) {

    }


}

