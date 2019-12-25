package cn.ifafu.ifafu.mvp.elec_main

import cn.ifafu.ifafu.base.i.IModel
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.entity.ElecQuery
import cn.ifafu.ifafu.entity.Selection
import io.reactivex.Observable

class ElecMainContract {

    interface View : IView {

        /**
         * @return 地区名称
         */
        fun getAreaText(): String

        /**
         * @return 楼栋名称
         */
        fun getBuildingText(): String

        /**
         * @return 楼层名称
         */
        fun getFloorText(): String

        /**
         * @return 充值金额
         */
        fun getPriceText(): String

        fun getRoomText(): String

        /**
         * @return 选择的电控名字
         */
        fun getCheckedDKName(): String

        fun setRoomText(room: String)

        fun setElecText(text: String)

        fun setSelectorView(i: Int, list: List<String>)

        fun setBalanceText(text: String)

        fun showElecCheckView()

        fun showPayView()

        fun initViewVisibility()

        fun showConfirmDialog(message: String)

        /**
         * 设置电控选项
         *
         * @param strings 电控名字
         */
        fun showDKSelection(strings: Collection<String>)

        /**
         * 设置电费选项信息，用于快捷查询的view初始化设置
         */
        fun setSelections(dkName: String, area: String, building: String, floor: String)

        fun openLoginActivity();
    }

    interface Presenter : IPresenter {

        fun onAreaSelect(name: String)

        fun onBuildingSelect(name: String)

        fun onFloorSelect(name: String)

        fun onDKSelect(aid: String)

        /**
         * 查询余额
         */
        fun queryCardBalance()

        /**
         * 查询电费
         */
        fun queryElecBalance()

        /**
         * 充值前弹窗
         */
        fun whetherPay()

        /**
         * 确认充值
         */
        fun pay()

    }

    interface Model : IModel {

        fun getSelectionFromJson(): List<Selection>

        /**
         * @return 查询数据
         */
        fun getQueryData(): ElecQuery?

        /**
         * 初始化Cookie
         *
         * @return 是否重新登录
         */
        fun initCookie(): Observable<Boolean>

        fun queryBalance(): Observable<Double>

        /**
         * 获取所有电控信息
         *
         * @return key: Name(电控名字) value: Aid(电控Id)
         */
        fun queryDKInfos(): Observable<Map<String, String>>

        /**
         * 查询电费
         */
        fun queryElectricity(data: ElecQuery): Observable<String>

        fun elecPay(data: ElecQuery, price: String): Observable<String>

        /**
         * 保存查询选项，用于一键查询
         */
        fun save(data: ElecQuery)
    }

}
