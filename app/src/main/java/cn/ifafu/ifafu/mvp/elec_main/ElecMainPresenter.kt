package cn.ifafu.ifafu.mvp.elec_main

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.data.entity.ElecQuery
import cn.ifafu.ifafu.data.entity.Selection
import cn.ifafu.ifafu.data.local.RepositoryImpl
import cn.ifafu.ifafu.util.AppUtils
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.util.SPUtils
import io.reactivex.Observable
import java.util.*

class ElecMainPresenter(view: ElecMainContract.View) :
        BasePresenter<ElecMainContract.View, ElecMainContract.Model>(
                view, ElecMainModel(view.context)
        ), ElecMainContract.Presenter {

    private var areaInfos: Selection? = null
    private var buildingInfos: Selection? = null
    private var floorInfos: Selection? = null

    private val dkInfos = mModel.getSelectionFromJson()
    private lateinit var elecQuery: ElecQuery

    private var dkNameToAidMap: Map<String, String> = HashMap()

    override fun onCreate() {
        elecQuery = mModel.getQueryData().run {
            if (this == null) {
                mView.openLoginActivity()
                mView.killSelf()
                return@onCreate
            } else {
                this
            }
        }

        // 检查登录态
        mCompDisposable.add(mModel.initCookie()
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doOnNext { mView.hideLoading() }
                .subscribe({ isReLogin ->
                    if (isReLogin) {
                        mView.showMessage("登录态失效，请重新登录")
                        mView.openLoginActivity()
                    } else {
                        // 查询余额
                        queryCardBalance(false)
                        // 初始化电控
                        intiElecCtrl()
                    }
                }, { this.onError(it) })
        )
    }

    /**
     * @return true MainActivity
     * false LoginActivity
     */
    private fun jumpJudge(): Boolean {
        val elecCookie = RepositoryImpl.getInstance().elecCookie
        val elecUser = RepositoryImpl.getInstance().elecUser
        if (!SPUtils.get(Constant.SP_ELEC).contain("IMEI")) {
            SPUtils.get(Constant.SP_ELEC).putString("IMEI", AppUtils.imei())
        }
        if (!SPUtils.get(Constant.SP_ELEC).contain("User-Agent")) {
            SPUtils.get(Constant.SP_ELEC).putString("User-Agent", AppUtils.getUserAgent())
        }
        return elecCookie != null && elecUser != null
    }

    /**
     * 初始化电控
     */
    private fun intiElecCtrl() {
        mCompDisposable.add(mModel.queryDKInfos()
                .doOnNext { stringStringMap -> dkNameToAidMap = stringStringMap }
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doOnNext { map -> mView.hideLoading() }
                .subscribe({
                    mView.showDKSelection(dkNameToAidMap.keys)
                    //设置快捷查询电费
                    quickQueryElec()
                }, { this.onError(it) })
        )
    }

    //设置快捷查询电费
    private fun quickQueryElec() {
        if (elecQuery.room == null || elecQuery.room.isEmpty())
            return
        for ((key, value) in dkNameToAidMap) {
            if (value == elecQuery.aid) {
                mView.setSelections(key, elecQuery.area, elecQuery.building, elecQuery.floor)
                if (elecQuery.area != null && !elecQuery.area.isEmpty()) {
                    onAreaSelect(elecQuery.area)
                }
                if (elecQuery.building != null && !elecQuery.building.isEmpty()) {
                    onBuildingSelect(elecQuery.building)
                }
                if (elecQuery.floor != null && !elecQuery.floor.isEmpty()) {
                    onFloorSelect(elecQuery.floor)
                }
                mView.setRoomText(elecQuery.room)
                break
            }
        }
        queryElecBalance()
    }

    /**
     * @param name   电控名字
     * @param isInit 是否初始化View和数据
     */
    private fun onDKChecked(name: String) {
        // 每次重选电控时，刷新界面与数据
        mView.initViewVisibility()
        var info = Selection()
        for (i in dkInfos) {
            if (name == i.name) {
                info = i
            }
        }
        areaInfos = info
        if (info.next == 1) {
            areaInfos = info
            mView.setSelectorView(1, getNames(info))
        } else if (info.next == 2) {
            buildingInfos = info
            mView.setSelectorView(2, getNames(info))
        } else if (info.next == 3) {
            floorInfos = info
            mView.setSelectorView(3, getNames(info))
        }
    }

    override fun onDKSelect(aid: String) {
        //如果存在Room，则设置不初始化mView
        onDKChecked(aid)
    }

    override fun onAreaSelect(name: String) {
        var info: Selection? = null
        for (datum in areaInfos!!.data) {
            if (datum.name == name) {
                info = datum
                break
            }
        }
        if (info != null) {
            if (info.next == 1) {
                areaInfos = info
                mView.setSelectorView(1, getNames(info))
            } else if (info.next == 2) {
                buildingInfos = info
                mView.setSelectorView(2, getNames(info))
            } else if (info.next == 3) {
                floorInfos = info
                mView.setSelectorView(3, getNames(info))
            } else {
                mView.showElecCheckView()
            }
        }
    }

    override fun onBuildingSelect(name: String) {
        var info: Selection? = null
        for (datum in buildingInfos!!.data) {
            if (datum.name == name) {
                info = datum
                break
            }
        }
        if (info != null) {
            if (info.next == 1) {
                areaInfos = info
                mView.setSelectorView(1, getNames(info))
            } else if (info.next == 2) {
                buildingInfos = info
                mView.setSelectorView(2, getNames(info))
            } else if (info.next == 3) {
                floorInfos = info
                mView.setSelectorView(3, getNames(info))
            } else {
                mView.showElecCheckView()
            }
        }
    }

    override fun onFloorSelect(name: String) {
        var info: Selection? = null
        for (datum in floorInfos!!.data) {
            if (datum.name == name) {
                info = datum
                break
            }
        }
        if (info != null) {
            if (info.next == 1) {
                areaInfos = info
                mView.setSelectorView(1, getNames(info))
            } else if (info.next == 2) {
                buildingInfos = info
                mView.setSelectorView(2, getNames(info))
            } else if (info.next == 3) {
                floorInfos = info
                mView.setSelectorView(3, getNames(info))
            } else {
                mView.showElecCheckView()
            }
        }
    }

    override fun queryCardBalance() {
        queryCardBalance(true)
    }

    /**
     * 查询余额
     *
     * @param showToast 是否显示Toast
     */
    private fun queryCardBalance(showToast: Boolean) {
        mCompDisposable.add(mModel.queryBalance()
                .map { aDouble -> String.format(Locale.CHINA, "%.2f", aDouble) }  // 取2位小数
                .compose(RxUtils.ioToMain())
                .doOnSubscribe {
                    if (showToast) {
                        mView.showLoading()
                    }
                }
                .doFinally {
                    if (showToast) {
                        mView.hideLoading()
                    }
                }
                .subscribe({ balance ->
                    mView.setBalanceText(balance)
                    if (showToast) {
                        mView.showMessage(String.format("当前账户余额：%s元", balance))
                    }
                }, { this.onError(it) })
        )
    }

    override fun queryElecBalance() {
        //设置电控信息
        val dkName = mView.getCheckedDKName()
        elecQuery.aid = dkNameToAidMap[dkName]
        //设置地区
        val area = mView.getAreaText()
        if (area.isNotEmpty() && areaInfos != null) {
            elecQuery.area = area
            for (areaInfo in areaInfos!!.data) {
                if (areaInfo.name == area) {
                    elecQuery.areaId = areaInfo.id
                }
            }
        }
        //设置楼栋
        val building = mView.getBuildingText()
        if (building.isNotEmpty() && buildingInfos != null) {
            elecQuery.building = building
            for (buildingInfo in buildingInfos!!.data) {
                if (buildingInfo.name == building) {
                    elecQuery.buildingId = buildingInfo.id
                }
            }
        }
        //设置楼层
        val floor = mView.getFloorText()
        if (floor.isNotEmpty() && floorInfos != null) {
            elecQuery.floor = floor
            for (floorInfo in floorInfos!!.data) {
                if (floorInfo.name == floor) {
                    elecQuery.floorId = floorInfo.id
                }
            }
        }
        //设置房间信息
        val room = mView.getRoomText()
        elecQuery.room = room
        if (room.isEmpty()) {
            mView.showMessage("请输入正确的宿舍号")
            return
        }
        mCompDisposable.add(mModel.queryElectricity(elecQuery)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ s ->
                    if (s.contains("无法")) {
                        mView.showMessage("$s，请检查信息是否正确")
                    } else {
                        mModel.save(elecQuery)
                        mView.setElecText(s)
                        mView.showMessage("查询成功")
                        mView.showPayView()
                    }
                }, { this.onError(it) })
        )
    }

    private fun getNames(info: Selection): List<String> {
        val list = ArrayList<String>()
        for (datum in info.data) {
            list.add(datum.name)
        }
        return list
    }

    override fun whetherPay() {
        val price = mView.getPriceText()
        if (price.isEmpty()) {
            mView.showMessage("请输入正确的金额")
            return
        }
        val msg = StringBuilder("请确认缴费信息：\n")
        if (elecQuery.area.isNotEmpty()) {
            msg.append("\t\t校区：").append(elecQuery.area)
        }
        if (elecQuery.building.isNotEmpty()) {
            msg.append("\n\t\t楼栋：").append(elecQuery.building)
        }
        if (elecQuery.floor.isNotEmpty()) {
            msg.append("\n\t\t楼层").append(elecQuery.floor)
        }
        msg.append("\n\t\t宿舍号：").append(elecQuery.room)
        msg.append("\n\t\t充值金额：")
                .append(price)
                .append("元\n\n注意事项：\n1、")
                .append(mView.activity.getString(R.string.elec_tos_1))
                .append("\n2、")
                .append(mView.activity.getString(R.string.elec_tos_2))
        mView.showConfirmDialog(msg.toString())
    }

    override fun pay() {
        mCompDisposable.add(Observable
                .fromCallable {
                    val price = Integer.valueOf(mView.getPriceText()) * 100
                    if (price <= 0) {
                        throw NumberFormatException()
                    }
                    price
                }
                .flatMap { mModel.elecPay(elecQuery, it.toString()) }
                .compose(RxUtils.computationToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ s ->
                    mView.showMessage(s)
                    queryCardBalance(false)
                }, { throwable ->
                    if (throwable is NumberFormatException) {
                        mView.showMessage("请输入正确金额")
                    } else {
                        onError(throwable)
                    }
                })
        )
    }

}
