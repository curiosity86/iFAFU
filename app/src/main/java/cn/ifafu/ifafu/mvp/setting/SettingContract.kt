package cn.ifafu.ifafu.mvp.setting

import cn.ifafu.ifafu.entity.GlobalSetting
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
        fun getSetting() : GlobalSetting

        fun save(setting: GlobalSetting)
    }

}