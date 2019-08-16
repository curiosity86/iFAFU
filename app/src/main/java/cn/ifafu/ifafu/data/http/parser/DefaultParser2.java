package cn.ifafu.ifafu.data.http.parser;

import java.util.Map;

import cn.ifafu.ifafu.data.entity.Response;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class DefaultParser2 extends BaseParser<Map<String, String>>{

    @Override
    public ObservableSource<Map<String, String>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> getHiddenParams(responseBody.string()));
    }
}
