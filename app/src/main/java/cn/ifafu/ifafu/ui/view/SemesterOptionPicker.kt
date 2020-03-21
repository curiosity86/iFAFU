package cn.ifafu.ifafu.ui.view

import android.content.Context
import android.graphics.Color
import cn.ifafu.ifafu.data.bean.Semester
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView

class SemesterOptionPicker(context: Context, onOptionSelectListener: (year: Int, term: Int) -> Unit) {

    private val mOptionPicker: OptionsPickerView<String> by lazy {
        OptionsPickerBuilder(context,
                OnOptionsSelectListener { options1, options2, _, _ ->
                    onOptionSelectListener(options1, options2)
                })
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择学年与学期")
                .setTitleColor(Color.parseColor("#157efb"))
                .setTitleSize(13)
                .build<String>()
    }

    fun show(semester: Semester) {
        mOptionPicker.setNPicker(semester.yearList, semester.termList, null)
        mOptionPicker.setSelectOptions(semester.yearIndex, semester.termIndex)
        mOptionPicker.show()
    }
}