package cn.ifafu.ifafu.mvp.add_exam;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.util.RxUtils;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class AddExamPresenter extends BasePresenter<AddExamContract.View, AddExamContract.Model> implements AddExamContract.Presenter {

    AddExamPresenter(AddExamContract.View view) {
        super(view, new AddExamModel(view.getContext()));
    }

    @Override
    public void onSave() {
        mCompDisposable.add(Observable
                .create((ObservableOnSubscribe<Integer>) emitter -> {
                    String name = mView.getNameText();
                    if (name.isEmpty()) {
                        emitter.onNext(1);
                        emitter.onComplete();
                        return;
                    }
                    String date = mView.getDateText();
                    if (date.isEmpty()) {
                        emitter.onNext(2);
                        emitter.onComplete();
                        return;
                    }
                    String address = mView.getAddressText();
                    String seat = mView.getSeatText();
                    String account = SPUtils.get(Constant.SP_USER_INFO).getString("account");
                    Exam exam = new Exam();
                    exam.setAccount(account);
                    exam.setName(name);
                    exam.setAddress(address);
                    exam.setDatetime(date);
                    exam.setLocal(true);
                    exam.setSeatNumber(seat);
                    mModel.save(exam);
                    emitter.onNext(0);
                    emitter.onComplete();
                })
                .compose(RxUtils.ioToMainScheduler())
                .subscribe(i -> {
                    if (i == 0){
                        mView.showMessage("保存成功");
                        mView.getActivity().setResult(200);
                        mView.killSelf();
                    } else if (i == 1) {
                        mView.showMessage(R.string.input_exam_name);
                    } else if (i == 2) {
                        mView.showMessage(R.string.input_exam_time);
                    }
                },this::onError)
        );

    }
}
