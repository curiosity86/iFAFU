package cn.ifafu.ifafu.mvp.notice;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Notice;
import cn.woolsen.android.mvp.BasePresenter;
import cn.woolsen.android.uitl.RxJavaUtils;
import io.reactivex.disposables.Disposable;

class NoticePresenter extends BasePresenter<NoticeContract.View, NoticeContract.Model>
        implements NoticeContract.Presenter {

    NoticePresenter(NoticeContract.View view) {
        super(view, new NoticeModel(view.getContext()));
    }

}
