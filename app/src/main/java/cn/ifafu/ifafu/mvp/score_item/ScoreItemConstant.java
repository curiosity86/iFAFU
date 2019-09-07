package cn.ifafu.ifafu.mvp.score_item;

import java.util.Map;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

public class ScoreItemConstant {

    public interface Presenter extends IPresenter {

    }

    public interface View extends IView {

        void setRvData(Map<String, String> data);
    }

    interface Model extends IModel {
        Score getScoreById(long id);
    }
}
