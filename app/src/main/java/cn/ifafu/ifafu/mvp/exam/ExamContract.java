package cn.ifafu.ifafu.mvp.exam;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import io.reactivex.Observable;

class ExamContract {

    interface Presenter extends IPresenter {

        void update();
    }

    interface View extends IView {
        void setSubtitle(String subtitle);

        void showEmptyView();

        void setExamAdapterData(List<Exam> data);
    }

    interface Model extends IZFModel {

        Observable<Response<List<Exam>>> getExamsFromNet(String year, String term);

        Observable<List<Exam>> getExamsFromDB(String year, String term);

        void save(List<Exam> list);

        void save(Exam exam);

        void delete(Exam exam);

        void delete(long id);
    }
}
