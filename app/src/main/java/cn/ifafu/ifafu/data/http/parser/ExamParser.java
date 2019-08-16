package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import cn.ifafu.ifafu.util.RegexUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class ExamParser extends BaseParser<Response<List<Exam>>> {

    private List<Exam> parse(String html) {
        Document document = Jsoup.parse(html);
        String account = getAccount(document);
        Elements elements = document.select("table[id=\"DataGrid1\"]").get(0)
                .getElementsByTag("tr");
        List<Exam> list = new ArrayList<>();
        Elements termAndYear = document.select("option[selected=\"selected\"]");
        String year = termAndYear.get(0).text();
        String term = termAndYear.get(1).text();
        for (int i = 1; i < elements.size(); i++) {
            try {
                Exam exam = getExam(elements.get(i).children());
                exam.setAccount(account);
                exam.setTerm(term);
                exam.setYear(year);
                list.add(exam);
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    private Exam getExam(Elements e) {
        List<Integer> numbers = RegexUtils.getNumbers(e.get(3).text());
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(numbers.get(0), numbers.get(1), numbers.get(2));
        calendar.set(Calendar.HOUR_OF_DAY, numbers.get(4));
        long start = calendar.getTime().getTime();
        calendar.set(Calendar.HOUR_OF_DAY, numbers.get(5));
        calendar.set(Calendar.MINUTE, numbers.get(6));
        long end = calendar.getTime().getTime();
        Exam exam = new Exam();
        exam.setId((long) e.get(0).text().hashCode());
        exam.setName(e.get(1).text());
        exam.setAddress(e.get(4).text());
        exam.setStartTime(start);
        exam.setEndTime(end);
        exam.setSeatNumber(e.get(6).text());
        return exam;
    }

    @Override
    public ObservableSource<Response<List<Exam>>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> {
            String html = responseBody.string();
            if (html.contains("请登录")) throw new NoAuthException();
            List<Exam> list = parse(html);
            Map<String, String> params = getHiddenParams(html);
            Response<List<Exam>> response = Response.success(list);
            response.setHiddenParams(params);
            return response;
        });
    }
}
