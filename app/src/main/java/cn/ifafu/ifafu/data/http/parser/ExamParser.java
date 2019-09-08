package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.util.RegexUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class ExamParser implements ObservableTransformer<String, Response<List<Exam>>> {

    private String account;
    private int schoolCode;

    public ExamParser(User user) {
        this.account = user.getAccount();
        this.schoolCode = user.getSchoolCode();
    }

    private List<Exam> parse(String html) {
        Document document = Jsoup.parse(html);
        Elements elementsTemp = document.select("table[id=\"DataGrid1\"]");
        if (elementsTemp.size() == 0) {
            return Collections.emptyList();
        }
        Elements elements = elementsTemp.get(0).getElementsByTag("tr");
        List<Exam> list = new ArrayList<>();
        Elements termAndYear = document.select("option[selected=\"selected\"]");
        String year = termAndYear.get(0).text();
        String term = termAndYear.get(1).text();
        for (int i = 1; i < elements.size(); i++) {
            try {
                Exam exam = getExam(elements.get(i).children());
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.set(Calendar.YEAR, numbers.get(0));
        calendar.set(Calendar.MONTH, numbers.get(1) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, numbers.get(2));
        calendar.set(Calendar.HOUR_OF_DAY, numbers.get(3));
        calendar.set(Calendar.MINUTE, numbers.get(4));
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
        exam.setAccount(account);

        return exam;
    }

    @Override
    public ObservableSource<Response<List<Exam>>> apply(Observable<String> upstream) {
        return upstream.map(html -> {
            List<Exam> list = parse(html);
            return Response.success(list);
        });
    }
}
