package cn.ifafu.ifafu.mvp.score_filter;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

class ScoreFilterConstant {

    interface Presenter extends IPresenter {

        void updateIES();
    }

    interface View extends IView {

        void setAdapterData(List<Score> list);

        void setIES(String ies);

        List<Score> getAdapterData();
    }

    interface Model extends IModel {

        List<Score> getScoresFromDB(String year, String term, String account);

        void save(Score score);

        void save(List<Score> scores);
    }
}
