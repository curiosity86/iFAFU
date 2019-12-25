package cn.ifafu.ifafu.data.http.parser;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.entity.Score;
import cn.ifafu.ifafu.entity.User;
import cn.ifafu.ifafu.util.NumUtils;

public class ScoreParser extends BaseParser<List<Score>> {

    private String account;
    private String schoolCode;

    public ScoreParser(User user) {
        this.account = user.getAccount();
        this.schoolCode = user.getSchoolCode();
    }

    @Override
    public List<Score> parse(@NotNull String html) {
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
                    list.add(paresToScoreFAFU(elements.get(i).children().eachText()));
                }
                break;
            case School.FAFU_JS:
                for (int i = 1; i < elements.size(); i++) {
                    list.add(paresToScoreFAFUJS(elements.get(i).children().eachText()));
                }
                break;
        }
        Collections.sort(list, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        for (Score score : list) {
            score.setAccount(account);
            score.setId(score.getId() * 31 + account.hashCode());
        }
        return list;
    }

    private Score paresToScoreFAFU(List<String> eles) {
        Score score = new Score();
        score.setYear(eles.get(0));
        score.setTerm(eles.get(1));
        score.setId(Long.parseLong(eles.get(2)));
        score.setName(eles.get(3));
        score.setNature(eles.get(4));
        score.setAttr(eles.get(5));
        if (!eles.get(6).isEmpty()) {
            score.setCredit(NumUtils.toFloat(eles.get(6)));
        }
        String ele7 = eles.get(7);
        if (ele7.contains("免修")) {
            score.setScore(Score.FREE_COURSE);
        } else {
            if (!eles.get(7).isEmpty()) {
                score.setScore(NumUtils.toFloat(eles.get(7)));
            }
        }
        System.out.println(JSONObject.toJSONString(eles));
        if (!eles.get(8).isEmpty()) {
            score.setMakeupScore(NumUtils.toFloat(eles.get(8)));
        }
        score.setRestudy(eles.get(9).isEmpty());
        score.setInstitute(eles.get(10));
        if (eles.size() > 13) {
            if (!eles.get(11).isEmpty()) {
                score.setGpa(NumUtils.toFloat(eles.get(11)));
            }
            score.setRemarks(eles.get(12));
            score.setMakeupRemarks(eles.get(13));
        } else {
            score.setRemarks(eles.get(11));
            score.setMakeupRemarks(eles.get(12));
        }
        score.setIESItem(score.getScore() != Score.FREE_COURSE
                && !score.getNature().contains("任意选修")
                && !score.getNature().contains("公共选修")
                && !score.getName().contains("体育"));
        return score;
    }

    private Score paresToScoreFAFUJS(List<String> eles) {
        Score score = new Score();
        score.setId(Long.parseLong(eles.get(2)));
        score.setYear(eles.get(0));
        score.setTerm(eles.get(1));
        score.setName(eles.get(3));
        score.setNature(eles.get(4));
        score.setAttr(eles.get(5));
        score.setCredit(NumUtils.toFloat(eles.get(6)));
        score.setGpa(NumUtils.toFloat(eles.get(7)));
        score.setScore(NumUtils.toFloat(eles.get(8)));
        score.setMakeupScore(NumUtils.toFloat(eles.get(10)));
        score.setInstitute(eles.get(12));
        try {
            score.setRemarks(eles.get(13));
            score.setMakeupRemarks(eles.get(14));
        } catch (Exception e) {
            e.printStackTrace();
        }
        score.setIESItem(!score.getNature().contains("任意选修") && !score.getName().contains("体育"));
        return score;
    }

}
