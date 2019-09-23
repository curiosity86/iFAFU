package cn.ifafu.ifafu.mvp.setting

import android.app.Activity
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.data.entity.Setting
import cn.ifafu.ifafu.view.adapter.syllabus_setting.CheckBoxItem

class SettingPresenter(view: SettingContract.View)
    : BasePresenter<SettingContract.View, SettingContract.Model>(view, SettingModel(view.context)),
        SettingContract.Presenter {

    private val setting = mModel.getSetting()

    private val settingHash = setting.hashCode()

    override fun onCreate() {
        mView.initRecycleView(listOf(CheckBoxItem("旧版主页主题", "应需求而来，喜欢0.9版本iFAFU界面就快来呀", setting.theme == Setting.THEME_OLD) {
            setting.theme = if (it) {
                Setting.THEME_OLD
            } else {
                Setting.THEME_NEW
            }
        }))
    }

    override fun onFinish() {
        if (settingHash != setting.hashCode()) {
            mModel.save(setting)
            mView.activity.setResult(Activity.RESULT_OK)
        }
    }

}
