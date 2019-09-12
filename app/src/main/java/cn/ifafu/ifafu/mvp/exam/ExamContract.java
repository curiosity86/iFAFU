package cn.ifafu.ifafu.mvp.exam;

import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import io.reactivex.Observable;

class ExamContract {

    interface Presenter extends IPresenter {

        void update();

        void switchYearTerm(int op1, int op2);
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

        Map<String, List<String>> getYearTermList();

        Map<String, String> getYearTerm();

        void save(List<Exam> list);

    }
}
