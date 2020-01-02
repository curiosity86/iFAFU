package cn.ifafu.ifafu.mvp.main

import cn.ifafu.ifafu.base.mvp.IModel
import cn.ifafu.ifafu.base.mvp.IPresenter
import cn.ifafu.ifafu.base.mvp.IView
import cn.ifafu.ifafu.entity.*
import io.reactivex.Observable

class BaseMainContract {

    interface View : IView {

        fun setCheckoutDialogData(users: List<User>)

        fun showCheckoutDialog()

        fun hideCheckoutDialog()
    }

    interface Presenter : IPresenter {

        fun updateApp()

        fun checkout()

        fun checkoutTheme()

        fun addAccountSuccess()

        fun deleteUser(user: User)

        fun checkoutTo(user: User)
    }

    interface Model: IModel {

        suspend fun getSetting(): GlobalSetting

        fun getCourses(): Observable<List<Course>>

        fun getThisTermExams(): List<Exam>

        /**
         *
         * @return [NextCourse.IN_COURSE]       正在上课
         * @return [NextCourse.HAS_NEXT_COURSE] 有下一节课
         * @return [NextCourse.NO_NEXT_COURSE]  今天的课上完了
         */
        fun getNextCourse(): Observable<NextCourse>

        fun getAllUser(): List<User>

        fun getLoginUser(): User?

        fun saveInUseUser(user: User)

        suspend fun deleteAccount(user: User)

        fun getWeather(cityCode: String): Observable<Weather>
    }

}