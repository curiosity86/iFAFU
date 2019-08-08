package cn.ifafu.ifafu.mvp.main

import android.graphics.drawable.Drawable

import cn.ifafu.ifafu.data.Menu
import cn.ifafu.ifafu.data.Weather
import cn.ifafu.ifafu.mvp.base.IZFModel
import cn.ifafu.ifafu.mvp.base.i.IPresenter
import cn.ifafu.ifafu.mvp.base.i.IView
import io.reactivex.Observable

class MainContract {

    interface View : IView {

        fun setMenuAdapterData(menus: List<Menu>)

        fun setLeftMenuHeadIcon(headIcon: Drawable)

        fun setLeftMenuHeadName(name: String)

        fun setWeatherText(weather: Weather)
    }

    interface Presenter : IPresenter {

        /**
         * 分享应用
         */
        fun shareApp()

        fun update()

        /**
         * 退出账号
         */
        fun quitAccount()
    }

    interface Model : IZFModel {

        /**
         * 获取主页菜单信息
         */
        fun getMenus(): Observable<List<Menu>>

        fun getSchoolIcon(): Drawable?

        fun getUserName(): String

        fun getWeather(cityCode: String): Observable<Weather>

        /**
         * used in DEBUG
         */
        fun clearAllDate()
    }

}
