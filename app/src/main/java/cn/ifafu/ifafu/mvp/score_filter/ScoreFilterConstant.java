package cn.ifafu.ifafu.mvp.score_filter;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.base.i.IModel;
import cn.ifafu.ifafu.base.i.IPresenter;
import cn.ifafu.ifafu.base.i.IView;
import io.reactivex.Observable;

class ScoreFilterConstant {

    interface Presenter extends IPresenter {

        void onCheck(Score score, boolean checked);

        void updateIES();
    }

    interface View extends IView {

        void setAdapterData(List<Score> list);

        void setIES(String ies);

    }

    interface Model extends IModel {

        Observable<List<Score>> getScoresFromDB(String year, String term);

        void save(Score score);

    }
}
