package cn.woolsen.android.uitl;

import android.annotation.SuppressLint;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * create by woolsen on 19/7/14
 */
public class RxJavaUtils {

    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        return Observable.create(source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressLint("CheckResult")
    public static <T> void quickRx(ObservableOnSubscribe<T> source, Consumer<T> consumer) {
        Observable.create(source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showToastLong(throwable.getMessage());
                    }
                });
    }

}
