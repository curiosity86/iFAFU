package cn.ifafu.ifafu.data.http.parser;

import cn.ifafu.ifafu.data.entity.Response;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class DefaultParser extends BaseParser<Response<String>>{
    @Override
    public ObservableSource<Response<String>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> {
            Response<String> response = Response.success("");
            response.setHiddenParams(getHiddenParams(responseBody.string()));
            return response;
        });
    }
}
