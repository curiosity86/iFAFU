package cn.ifafu.ifafu.ui.view

import android.content.Context
import android.graphics.Color
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView

class SemesterPicker(context: Context, onOptionSelectListener: (year: String, term: String) -> Unit) {

    private lateinit var yearList: List<String>
    private lateinit var termList: List<String>

    var yearIndex = 0
        private set
    var termIndex = 0
        private set
    val yearStr: String
        get() = yearList[yearIndex]
    val termStr: String
        get() = termList[termIndex]

    private val mOptionPicker: OptionsPickerView<String> =
            OptionsPickerBuilder(context,
                    OnOptionsSelectListener { options1, options2, _, _ ->
                        onOptionSelectListener(yearList[options1], termList[options2])
                    })
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("请选择学年与学期")
                    .setTitleColor(Color.parseColor("#157efb"))
                    .setTitleSize(13)
                    .build<String>()

    fun setIndex(yearIndex: Int, termIndex: Int) {
        this.yearIndex = yearIndex
        this.termIndex = termIndex
        mOptionPicker.setSelectOptions(yearIndex, termIndex)
    }

    fun setSelections(yearList: List<String>, termList: List<String>) {
        this.yearList = yearList
        this.termList = termList
        mOptionPicker.setNPicker(yearList, termList, null)
    }

    fun show() {
        mOptionPicker.show()
    }
}