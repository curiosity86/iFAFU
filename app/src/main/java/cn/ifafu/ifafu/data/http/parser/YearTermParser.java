package cn.ifafu.ifafu.data.http.parser;

import java.util.List;

import cn.ifafu.ifafu.data.entity.YearTerm;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class YearTermParser extends BaseParser<List<YearTerm>> {
    @Override
    public ObservableSource<List<YearTerm>> apply(Observable<ResponseBody> upstream) {
        return null;
    }
}
