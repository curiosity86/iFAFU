package cn.ifafu.ifafu.mvp.main

import android.graphics.drawable.Drawable
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.mvp.base.i.IPresenter
import cn.ifafu.ifafu.mvp.base.i.IView
import cn.ifafu.ifafu.mvp.base.i.IZFModel
import cn.ifafu.ifafu.view.timeline.TimeAxis
import io.reactivex.Observable

class MainContract {

    interface View : IView {

        fun setMenuAdapterData(menus: List<Menu>)

        fun setLeftMenuHeadIcon(headIcon: Drawable)

        fun setLeftMenuHeadName(name: String)

        fun setWeatherText(weather: Pair<String, String>)

        fun setCourseText(title: String, name: String, address: String, time: String)

        fun setTimeLineData(data: List<TimeAxis>)

        fun setCheckoutDialogData(users: List<User>)

        fun showCheckoutDialog()

        fun hideCheckoutDialog()
    }

    interface Presenter : IPresenter {

        fun updateApp()

        fun shareApp()

        fun onRefresh()

        fun updateWeather()

        fun updateNextCourseView()

        fun updateTimeLine()

        fun checkout()

        fun addAccountSuccess();

        fun deleteUser(user: User)

        fun checkoutTo(user: User)
    }

    interface Model : IZFModel {

        fun getAllUser(): List<User>

        fun getThisTermExams(): List<Exam>

        /**
         * 获取主页菜单信息
         */
        fun getMenus(): Observable<List<Menu>>

        fun getSchoolIcon(): Drawable

        fun getUserName(): String

        fun getHoliday(): List<Holiday>

        fun getLoginUser(): User?

        fun saveLoginUser(user: User)

        fun deleteAccount(user: User)

        fun getWeather(cityCode: String): Observable<Weather>

        /**
         * used in DEBUG
         */
        fun clearAllDate()
    }

}
