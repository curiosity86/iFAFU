package cn.ifafu.ifafu.util;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {

    /**
     * io,main线程调度器
     */
    public static <T> ObservableTransformer<T, T> ioToMainScheduler() {
        return new IOMainTransformer<T>();
    }

    static class IOMainTransformer<T> implements ObservableTransformer<T, T> {
        @Override
        public ObservableSource<T> apply(Observable<T> upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

}
