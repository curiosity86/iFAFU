package cn.ifafu.ifafu.data.http.parser;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class ParamsParser extends BaseParser<Map<String, String>>{

    @Override
    public ObservableSource<Map<String, String>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> getHiddenParams(responseBody.string()));
    }
}
