package cn.ifafu.ifafu.mvp.setting

import cn.ifafu.ifafu.data.entity.Setting
import cn.ifafu.ifafu.base.i.IModel
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.base.i.IView

class SettingContract {

    interface View : IView {
        fun initRecycleView(items: List<Any>)
    }

    interface Presenter : IPresenter {
        fun onFinish()
    }

    interface Model : IModel {
        fun getSetting() : Setting

        fun save(setting: Setting)
    }

}