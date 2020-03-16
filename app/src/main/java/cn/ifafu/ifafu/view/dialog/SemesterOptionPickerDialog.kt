package cn.ifafu.ifafu.view.dialog

import cn.ifafu.ifafu.data.bean.Semester
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

