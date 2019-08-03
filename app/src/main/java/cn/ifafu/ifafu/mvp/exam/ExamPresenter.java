package cn.ifafu.ifafu.mvp.exam;

import cn.woolsen.android.mvp.BasePresenter;

public class ExamPresenter extends BasePresenter<ExamContract.View, ExamContract.Model>
        implements ExamContract.Presenter {

    public ExamPresenter(ExamContract.View view) {
        super(view, new ExamModel(view.getContext()));
//        examController = ((IFAFU) mView.getActivity().getApplication()).getExamController();
    }

    @Override
    public void onStart() {
    }
}
