package cn.ifafu.ifafu.mvp.syllabus_setting

import android.widget.ImageView
import cn.ifafu.ifafu.base.mvp.IModel
import cn.ifafu.ifafu.base.mvp.IPresenter
import cn.ifafu.ifafu.base.mvp.IView
import cn.ifafu.ifafu.entity.SyllabusSetting

class SyllabusSettingContract {

    interface View : IView {

        fun initRecycleView(items: List<Any>)

        fun showPicturePicker()

        fun showColorPicker(setting: SyllabusSetting, ivColor: ImageView)
    }

    interface Presenter : IPresenter {

        fun onPictureSelect(uri: String)

        suspend fun onFinish()
    }

    interface Model : IModel {

        fun getSetting(): SyllabusSetting

        suspend fun save(setting: SyllabusSetting)
    }

}
