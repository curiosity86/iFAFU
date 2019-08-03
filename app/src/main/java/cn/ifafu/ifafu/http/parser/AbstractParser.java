package cn.ifafu.ifafu.http.parser;

import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public abstract class AbstractParser<T> implements ObservableTransformer<String, T>{

    abstract T parse(String html);

    @Override
    public ObservableSource<T> apply(Observable<String> upstream) {
        return upstream.map(this::parse);
    }
}
