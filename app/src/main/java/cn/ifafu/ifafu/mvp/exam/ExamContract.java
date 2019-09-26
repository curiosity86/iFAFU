package cn.ifafu.ifafu.mvp.exam;

import java.util.List;

import cn.ifafu.ifafu.base.i.IPresenter;
import cn.ifafu.ifafu.base.i.IView;
import cn.ifafu.ifafu.base.ifafu.IZFModel;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.YearTerm;
import io.reactivex.Observable;
import kotlin.Pair;

class ExamContract {

    interface Presenter extends IPresenter {

        void update();

        void switchYearTerm(String op1, String op2);
    }

    interface View extends IView {

        void showEmptyView();

        void setYearTermOptions(int option1, int option2);

        void setExamAdapterData(List<Exam> data);

        void setYearTermData(List<String> years, List<String> terms);
    }

    interface Model extends IZFModel {

        Observable<List<Exam>> getExamsFromNet(String year, String term);

        Observable<List<Exam>> getExamsFromDB(String year, String term);

        Observable<YearTerm> getYearTermList();

        Pair<String, String> getYearTerm();

        void save(List<Exam> list);

    }
}
