package cn.ifafu.ifafu.mvp.syllabus_setting

import android.widget.ImageView
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.base.i.IModel
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.base.i.IView

class SyllabusSettingContract {

    interface View : IView {

        fun initRecycleView(items: List<Any>)

        fun showPicturePicker()

        fun showColorPicker(setting: SyllabusSetting, ivColor: ImageView)
    }

    interface Presenter : IPresenter {

        fun onPictureSelect(uri: String)

        fun onFinish()
    }

    interface Model : IModel {

        fun getSetting(): SyllabusSetting

        fun save(setting: SyllabusSetting)
    }

}
