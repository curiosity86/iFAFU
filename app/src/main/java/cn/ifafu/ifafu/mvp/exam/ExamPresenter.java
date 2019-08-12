package cn.ifafu.ifafu.mvp.exam;

import cn.ifafu.ifafu.mvp.base.BasePresenter;

class ExamPresenter extends BasePresenter<ExamContract.View, ExamContract.Model>
        implements ExamContract.Presenter {

    ExamPresenter(ExamContract.View view) {
        mView = view;
        mModel = new ExamModel(view.getContext());
    }

    @Override
    public void onStart() {
    }
}
