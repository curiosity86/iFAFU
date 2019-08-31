package cn.ifafu.ifafu.electricity.main;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.electricity.data.QueryData;
import cn.ifafu.ifafu.electricity.data.Selection;
import io.reactivex.Observable;

class ElecMainContract {

    interface View extends IView {

        void setSnoText(String text);

        void setElecText(String text);

        void setSelectorView(int i, List<String> list);

        void setBalanceText(String text);

        void showElecCheckView();

        void showPayView();

        void initViewVisibility();

        void showConfirmDialog(String message);

        /**
         * 设置电控选项
         *
         * @param strings 电控名字
         */
        void showDKSelection(Collection<String> strings);

        void setRoomText(String text);

        /**
         * 设置电费选项信息，用于快捷查询的view初始化设置
         */
        void setSelections(String dkName, String area, String building, String floor);

        /**
         * @return 地区名称
         */
        String getAreaText();

        /**
         * @return 楼栋名称
         */
        String getBuildingText();

        /**
         * @return 楼层名称
         */
        String getFloorText();

        /**
         * @return 充值金额
         */
        String getPriceText();

        /**
         * @return 宿舍号
         */
        String getRoomText();

        /**
         * @return 选择的电控名字
         */
        String getCheckedDKName();
    }

    interface Presenter extends IPresenter {

        void quit();

        void onAreaSelect(String name);

        void onBuildingSelect(String name);

        void onFloorSelect(String name);

        void onDKSelect(String aid);

        /**
         * 查询余额
         */
        void queryCardBalance();

        /**
         * 查询电费
         */
        void queryElecBalance();

        /**
         * 充值前弹窗
         */
        void whetherPay();

        /**
         * 确认充值
         */
        void pay();

    }

    interface Model extends IModel {

        /**
         * 清除所有数据
         */
        void clearAll();

        /**
         * 获取当前账号的学号
         */
        String getSno();

        /**
         * 获取当前账号
         */
        String getAccount();

        /**
         * 初始化Cookie
         *
         * @return 是否重新登录
         */
        Observable<Boolean> initCookie();

        List<Selection> getSelectionFromJson();

        Observable<Double> queryBalance();

        /**
         * 获取所有电控信息
         *
         * @return key: Name(电控名字) value: Aid(电控Id)
         */
        Observable<Map<String, String>> queryDKInfos();

        /**
         * 充值电费
         */
        Observable<String> queryElec(QueryData data);

        Observable<String> elecPay(QueryData data, String price);

        /**
         * 保存查询选项，用于一键查询
         */
        void save(QueryData data);

        /**
         * @return 一键查询数据
         */
        QueryData getQueryData();
    }

}
