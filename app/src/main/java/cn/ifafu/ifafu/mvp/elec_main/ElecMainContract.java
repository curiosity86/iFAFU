package cn.ifafu.ifafu.mvp.elec_main;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.ElecQuery;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.data.entity.Selection;
import io.reactivex.Observable;

public class ElecMainContract {

    interface View extends IView {

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

    public interface Presenter extends IPresenter {

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

        String getAccount();

        String getXfbAccount();
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
         * 查询电费
         */
        Observable<String> queryElectricity(ElecQuery data);

        Observable<String> elecPay(ElecQuery data, String price);

        /**
         * 保存查询选项，用于一键查询
         */
        void save(ElecQuery data);

        /**
         * @return 一键查询数据
         */
        ElecQuery getQueryData();
    }

}
