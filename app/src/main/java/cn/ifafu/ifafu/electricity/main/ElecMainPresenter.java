package cn.ifafu.ifafu.electricity.main;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.ifafu.ifafu.electricity.data.QueryData;
import cn.ifafu.ifafu.electricity.data.Selection;
import cn.ifafu.ifafu.electricity.login.ElecLoginActivity;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

public class ElecMainPresenter extends BasePresenter<ElecMainContract.View, ElecMainContract.Model>
        implements ElecMainContract.Presenter {

    private Selection areaInfos;
    private Selection buildingInfos;
    private Selection floorInfos;

    private final List<Selection> dkInfos;
    private QueryData userQueryData;

    private Map<String, String> dkNameToAidMap = new HashMap<>();

    ElecMainPresenter(ElecMainContract.View view) {
        super(view, new ElecMainModel(view.getContext()));
        dkInfos = mModel.getSelectionFromJson();
    }

    @Override
    public void onStart() {
        // 检查登录态
        checkLoginStatus();
        // 设置学号
        mView.setSnoText(mModel.getSno());
    }

    /**
     * 检查登录态，失效则跳转到登陆界面
     */
    private void checkLoginStatus() {
        mCompDisposable.add(mModel.initCookie()
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .subscribe(isReLogin -> {
                    if (isReLogin) {
                        mView.showMessage("登录态失效，请重新登录");
                        mView.openActivity(new Intent(mView.getContext(), ElecLoginActivity.class));
                        mView.killSelf();
                    } else {
                        mView.setSnoText(mModel.getSno());
                        // 查询余额
                        queryCardBalance(false);
                        // 初始化电控
                        intiElecCtrl();
                    }
                }, this::onError)
        );
    }

    /**
     * 初始化电控
     */
    private void intiElecCtrl() {
        mCompDisposable.add(mModel.queryDKInfos()
                .doOnNext(stringStringMap -> dkNameToAidMap = stringStringMap)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doOnNext(map -> mView.hideLoading())
                .subscribe(s -> {
                    mView.showDKSelection(dkNameToAidMap.keySet());
                    //设置快捷查询电费
                    quickQueryElec();
                }, this::onError)
        );
    }

    //设置快捷查询电费
    private void quickQueryElec() {
        userQueryData = mModel.getQueryData();
        if (userQueryData == null || userQueryData.getRoom() == null || userQueryData.getRoom().isEmpty())
            return;
        for (Map.Entry<String, String> e : dkNameToAidMap.entrySet()) {
            if (e.getValue().equals(userQueryData.getAid())) {
                mView.setSelections(e.getKey(), userQueryData.getArea(), userQueryData.getBuilding(), userQueryData.getFloor());
                if (userQueryData.getArea() != null && !userQueryData.getArea().isEmpty()) {
                    onAreaSelect(userQueryData.getArea());
                }
                if (userQueryData.getBuilding() != null && !userQueryData.getBuilding().isEmpty()) {
                    onBuildingSelect(userQueryData.getBuilding());
                }
                if (userQueryData.getFloor() != null && !userQueryData.getFloor().isEmpty()) {
                    onFloorSelect(userQueryData.getFloor());
                }
                mView.setRoomText(userQueryData.getRoom());
                break;
            }
        }
        queryElecBalance();
    }

    /**
     * @param name   电控名字
     * @param isInit 是否初始化View和数据
     */
    private void onDKChecked(String name, boolean isInit) {
        // 每次重选电控时，刷新界面与数据
        if (isInit) {
            mView.initViewVisibility();
        }
        Selection info = new Selection();
        for (Selection i : dkInfos) {
            if (name.equals(i.getName())) {
                info = i;
            }
        }
        areaInfos = info;
        if (info.getNext() == 1) {
            areaInfos = info;
            mView.setSelectorView(1, getNames(info));
        } else if (info.getNext() == 2) {
            buildingInfos = info;
            mView.setSelectorView(2, getNames(info));
        } else if (info.getNext() == 3) {
            floorInfos = info;
            mView.setSelectorView(3, getNames(info));
        }
    }

    @Override
    public void onDKSelect(String name) {
        //如果存在Room，则设置不初始化mView
        onDKChecked(name, true);
    }

    @Override
    public void onAreaSelect(String name) {
        Selection info = null;
        for (Selection datum : areaInfos.getData()) {
            if (datum.getName().equals(name)) {
                info = datum;
                break;
            }
        }
        if (info != null) {
            if (info.getNext() == 1) {
                areaInfos = info;
                mView.setSelectorView(1, getNames(info));
            } else if (info.getNext() == 2) {
                buildingInfos = info;
                mView.setSelectorView(2, getNames(info));
            } else if (info.getNext() == 3) {
                floorInfos = info;
                mView.setSelectorView(3, getNames(info));
            } else {
                mView.showElecCheckView();
            }
        }
    }

    @Override
    public void onBuildingSelect(String name) {
        Selection info = null;
        for (Selection datum : buildingInfos.getData()) {
            if (datum.getName().equals(name)) {
                info = datum;
                break;
            }
        }
        if (info != null) {
            if (info.getNext() == 1) {
                areaInfos = info;
                mView.setSelectorView(1, getNames(info));
            } else if (info.getNext() == 2) {
                buildingInfos = info;
                mView.setSelectorView(2, getNames(info));
            } else if (info.getNext() == 3) {
                floorInfos = info;
                mView.setSelectorView(3, getNames(info));
            } else {
                mView.showElecCheckView();
            }
        }
    }

    @Override
    public void onFloorSelect(String name) {
        Selection info = null;
        for (Selection datum : floorInfos.getData()) {
            if (datum.getName().equals(name)) {
                info = datum;
                break;
            }
        }
        if (info != null) {
            if (info.getNext() == 1) {
                areaInfos = info;
                mView.setSelectorView(1, getNames(info));
            } else if (info.getNext() == 2) {
                buildingInfos = info;
                mView.setSelectorView(2, getNames(info));
            } else if (info.getNext() == 3) {
                floorInfos = info;
                mView.setSelectorView(3, getNames(info));
            } else {
                mView.showElecCheckView();
            }
        }
    }

    @Override
    public void queryCardBalance() {
        queryCardBalance(true);
    }

    /**
     * 查询余额
     *
     * @param showToast 是否显示Toast
     */
    private void queryCardBalance(boolean showToast) {
        mCompDisposable.add(mModel.queryBalance()
                .map(aDouble -> String.format(Locale.CHINA, "%.2f", aDouble))  // 取2位小数
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> {
                    if (showToast) {
                        mView.showLoading();
                    }
                })
                .doFinally(() -> {
                    if (showToast) {
                        mView.hideLoading();
                    }
                })
                .subscribe(balance -> {
                    mView.setBalanceText(balance);
                    if (showToast) {
                        mView.showMessage(String.format("当前账户余额：%s元", balance));
                    }
                }, this::onError)
        );
    }

    @Override
    public void queryElecBalance() {
        QueryData userQuery = new QueryData();
        // 设置账号
        userQuery.setAccount(mModel.getAccount());
        //设置电控信息
        String dkName = mView.getCheckedDKName();
        userQuery.setAid(dkNameToAidMap.get(dkName));
        //设置地区
        String area = mView.getAreaText();
        if (!area.isEmpty() && areaInfos != null) {
            userQuery.setArea(area);
            for (Selection areaInfo : areaInfos.getData()) {
                if (areaInfo.getName().equals(area)) {
                    userQuery.setAreaId(areaInfo.getId());
                }
            }
        }
        //设置楼栋
        String building = mView.getBuildingText();
        if (!building.isEmpty() && buildingInfos != null) {
            userQuery.setBuilding(building);
            for (Selection buildingInfo : buildingInfos.getData()) {
                if (buildingInfo.getName().equals(building)) {
                    userQuery.setBuildingId(buildingInfo.getId());
                }
            }
        }
        //设置楼层
        String floor = mView.getFloorText();
        if (!floor.isEmpty() && floorInfos != null) {
            userQuery.setFloor(building);
            for (Selection floorInfo : floorInfos.getData()) {
                if (floorInfo.getName().equals(floor)) {
                    userQuery.setFloorId(floorInfo.getId());
                }
            }
        }
        //设置房间信息
        String room = mView.getRoomText();
        userQuery.setRoom(room);
        if (room.isEmpty()) {
            mView.showMessage("请输入正确的宿舍号");
            return;
        }
        mCompDisposable.add(mModel.queryElec(userQuery)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(s -> {
                    if (s.contains("无法")) {
                        this.userQueryData = null;
                        mView.showMessage(s + "，请检查信息是否正确");
                    } else {
                        this.userQueryData = userQuery;
                        mModel.save(userQueryData);
                        mView.setElecText(s);
                        mView.showMessage("查询成功");
                        mView.showPayView();
                    }
                }, this::onError)
        );
    }

    private List<String> getNames(Selection info) {
        List<String> list = new ArrayList<>();
        for (Selection datum : info.getData()) {
            list.add(datum.getName());
        }
        return list;
    }

    @Override
    public void whetherPay() {
        String price = mView.getPriceText();
        if (price.isEmpty()) {
            mView.showMessage("请输入正确的金额");
            return;
        }
        StringBuilder msg = new StringBuilder("请确认缴费信息：\n");
        if (!userQueryData.getArea().isEmpty()) {
            msg.append("\t\t校区：").append(userQueryData.getArea()).append("\n");
        }
        if (!userQueryData.getBuilding().isEmpty()) {
            msg.append("\t\t楼栋：").append(userQueryData.getBuilding()).append("\n");
        }
        if (!userQueryData.getFloor().isEmpty()) {
            msg.append("\t\t楼层").append(userQueryData.getFloor()).append("\n");
        }
        msg.append("\t\t宿舍号：").append(userQueryData.getRoom()).append("\n");
        msg.append("\t\t充值金额：").append(mView.getPriceText()).append("元");
        mView.showConfirmDialog(msg.toString());
    }

    @Override
    public void pay() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    int price = Integer.valueOf(mView.getPriceText()) * 100;
                    if (price <= 0) {
                        throw new NumberFormatException();
                    }
                    return price;
                })
                .flatMap(integer -> mModel.elecPay(userQueryData, String.valueOf(integer)))
                .compose(RxUtils.computationToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(s -> {
                            mView.showMessage(s);
                            queryCardBalance(false);
                        }, throwable -> {
                            if (throwable instanceof NumberFormatException) {
                                mView.showMessage("请输入正确金额");
                            }
                        }
                )
        );
    }

    @Override
    public void quit() {
        mModel.clearAll();
        mView.openActivity(new Intent(mView.getContext(), ElecLoginActivity.class));
        mView.killSelf();
    }

}
