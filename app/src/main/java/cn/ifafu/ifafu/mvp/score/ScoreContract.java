package cn.ifafu.ifafu.mvp.score;

import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import io.reactivex.Observable;

class ScoreContract {

    interface View extends IView {
        void setYearTermOptions(int option1, int option2);

        void setRvScoreData(List<Score> data);

        void setYearTermData(List<String> years, List<String> terms);

        void setYearTermTitle(String year, String term);

        void setIESText(String big, String little);

        void setCntText(String big, String little);

        void setGPAText(String text);
    }

    interface Presenter extends IZFPresenter {

        void updateFromNet();

        void switchYearTerm(int op1, int op2);

        void openFilterActivity();

        void updateIES();
    }

    interface Model extends IZFModel {

        Observable<Response<List<Score>>> getScoresFromNet(String year, String term);

        Observable<Response<List<Score>>> getScoresFromNet();

        List<Score> getScoresFromDB(String year, String term);

        Observable<Map<String, List<String>>> getYearTermList();

        Observable<Map<String, String>> getYearTerm();

        void save(List<Score> list);

        void delete(String year, String term);

        void deleteAllOnlineCourse();
    }

}
