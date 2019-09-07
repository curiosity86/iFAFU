package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class ScoreParser extends BaseParser<List<Score>> {

    private String account;
    private int schoolCode;

    public ScoreParser(User user) {
        this.account = user.getAccount();
        this.schoolCode = user.getSchoolCode();
    }

    private List<Score> parse(String html) {
        List<Score> list = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Elements elementsTemp = document.select("table[id=\"Datagrid1\"]");
        if (elementsTemp.size() == 0) {
            return Collections.emptyList();
        }
        Elements elements = elementsTemp.get(0).getElementsByTag("tr");
        switch (schoolCode) {
            case School.FAFU:
                for (int i = 1; i < elements.size(); i++) {
                    try {
                        list.add(paresToScoreFAFU(elements.get(i).children()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case School.FAFU_JS:
                for (int i = 1; i < elements.size(); i++) {
                    try {
                        list.add(paresToScoreFAFUJS(elements.get(i).children()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        Collections.sort(list, (o1, o2) -> o1.getId().compareTo(o2.getId()));
        for (Score score : list) {
            score.setAccount(account);
            score.setId(score.getId() * 31 + account.hashCode());
        }
        return list;
    }

    private Score paresToScoreFAFU(Elements eles) {
        Score score = new Score();
        score.setYear(eles.get(0).text());
        score.setTerm(eles.get(1).text());
        score.setId(Long.parseLong(eles.get(2).text()));
        score.setName(eles.get(3).text());
        score.setNature(eles.get(4).text());
        score.setAttr(eles.get(5).text());
        score.setCredit(Float.parseFloat(eles.get(6).text()));
        score.setScore(Float.parseFloat(eles.get(7).text()));
        try {
            float makeupScore = Float.parseFloat(eles.get(8).text());
            score.setMakeupScore(makeupScore);
        } catch (Exception ignored) {
        }
        if (!eles.get(9).text().isEmpty()) {
            score.setRestudy(false);
        } else {
            score.setRestudy(true);
        }
        score.setInstitute(eles.get(10).text());
        score.setGpa(Float.parseFloat(eles.get(11).text()));
        score.setRemarks(eles.get(12).text());
        score.setMakeupRemarks(eles.get(13).text());
        score.setIsIESItem(!score.getNature().contains("任意选修") && !score.getName().contains("体育"));
        return score;
    }

    private Score paresToScoreFAFUJS(Elements eles) {
        Score score = new Score();
        score.setYear(eles.get(0).text());
        score.setTerm(eles.get(1).text());
        score.setId(Long.parseLong(eles.get(2).text()));
        score.setName(eles.get(3).text());
        score.setNature(eles.get(4).text());
        score.setAttr(eles.get(5).text());
        score.setCredit(Float.parseFloat(eles.get(6).text()));
        score.setGpa(Float.parseFloat(eles.get(7).text()));
        score.setScore(Float.parseFloat(eles.get(8).text()));
        try {
            float makeupScore = Float.parseFloat(eles.get(10).text());
            score.setMakeupScore(makeupScore);
        } catch (Exception ignored) {
            score.setMakeupScore(0F);
        }
        score.setInstitute(eles.get(12).text());
        score.setRemarks(eles.get(13).text());
        score.setMakeupRemarks(eles.get(14).text());
        score.setIsIESItem(!score.getNature().contains("任意选修") && !score.getName().contains("体育"));
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
