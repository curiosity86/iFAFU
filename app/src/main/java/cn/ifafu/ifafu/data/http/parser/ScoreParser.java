package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class ScoreParser extends BaseParser<List<Score>> {

    public List<Score> parse(String html) {
        Document document = Jsoup.parse(html);
        Elements elementsTemp = document.select("table[id=\"DataGrid1\"]");
        if (elementsTemp.size() == 0) {
            return Collections.emptyList();
        }
        Elements elements = elementsTemp.get(0).getElementsByTag("tr");
        List<Score> list = new ArrayList<>();
        Elements termAndYear = document.select("option[selected=\"selected\"]");
        String account = getAccount(document);
        for (int i = 1; i < elements.size(); i++) {
            try {
                Score score = getScore(elements.get(i).children());
                score.setAccount(account);
                list.add(score);
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    private Score getScore(Elements e) {
        Score score = new Score();
        score.setYear(e.get(0).text());
        score.setTerm(e.get(1).text());
        score.setId(Long.parseLong(e.get(2).text()));
        score.setName(e.get(3).text());
        score.setNature(e.get(4).text());
        score.setAttr(e.get(5).text());
        score.setCredit(Float.parseFloat(e.get(6).text()));
        score.setScore(Float.parseFloat(e.get(7).text()));
        try {
            float makeupScore = Float.parseFloat(e.get(8).text());
            score.setMakeupScore(makeupScore);
        } catch (Exception ignored) {
            score.setMakeupScore(0F);
        }
        if (e.get(9).text().isEmpty()) {
            score.setRestudy(false);
        } else {
            score.setRestudy(true);
        }
        score.setInstitute(e.get(10).text());
        score.setGpa(Float.parseFloat(e.get(11).text()));
        score.setRemarks(e.get(12).text());
        score.setMakeupRemarks(e.get(13).text());
        return score;
    }

    @Override
    public ObservableSource<List<Score>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> {
            String html = responseBody.string();
            if (html.contains("请登录")) {
                throw new NoAuthException();
            }
            return parse(html);
        });
    }

}
