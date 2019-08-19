package cn.ifafu.ifafu.data.http.parser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import cn.ifafu.ifafu.util.RegexUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class SyllabusParser extends BaseParser<List<Course>> {

    private String[] weekdayCN = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    private int[] weekday = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
    //用于标记课程位置
    private boolean[][] locFlag = new boolean[20][8];

    private String account;

    public List<Course> parse(String html) {
        List<Course> courses = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        account = getAccount(doc);
        Elements nodeTrs = doc.getElementById("Table1").getElementsByTag("tr");
        Help help = new Help();
        for (int i = 2; i < nodeTrs.size(); i++) {
            //定位到课程元素
            Elements tds = nodeTrs.get(i).getElementsByTag("td");
            //开始节数的备用方案，通过解析侧边节数获取
            int index = 0;
            while (index < 2) {
                String s = tds.get(index).text();
                String nodeRegex = "第[0-9]{1,2}节";
                if (s.matches(nodeRegex)) {
                    help.beginNode = RegexUtils.getNumbers(s).get(0);
                    index++;
                    break;
                }
                index++;
            }
            //解析课程信息
            for (int j = 0; j < tds.size() - index; j++) {
                Element td = tds.get(index + j);
                //列的备用方案
                help.col = j;
//                Log.d("SyllabusParser", "help => col = " + help.col);
                for (int k = 1; k < j; k++) {
                    if (locFlag[help.beginNode][k]) {
                        help.col++;
                    }
                }
                //课程节数的备用方案，通过“rowspan“获取
                if (td.hasAttr("rowspan")) {
                    help.nodeNum = Integer.parseInt(td.attr("rowspan"));
                }
                List<Course> clist = parseTdElement(td, help);
                courses.addAll(clist);
            }
            for (Course c : courses) {
                mark(c);
            }
        }
        return merge(courses);
    }

    private List<Course> merge(List<Course> courses) {
        List<Course> newList = new ArrayList<>();
        Map<String, List<Course>> map = new HashMap<>();
        for (Course course : courses) {
            String key = course.getName() + course.getTeacher() + course.getAddress() + course.getWeekday()
                    + course.getWeekSet().first() + course.getWeekSet().last();
            if (map.get(key) == null) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(course);
        }
        for (List<Course> cl : map.values()) {
            Collections.sort(cl, (o1, o2) -> Integer.compare(o1.getBeginNode(), o2.getBeginNode()));
            if (cl.size() == 2 && cl.get(0).getBeginNode() + cl.get(0).getNodeCnt() == cl.get(1).getBeginNode()) {
                cl.get(0).setNodeCnt(cl.get(0).getNodeCnt() + cl.get(1).getNodeCnt());
                cl.remove(1);
            }
            newList.addAll(cl);
        }
        return newList;
    }

    private List<Course> parseTdElement(Element td, Help help) {
        List<Course> list = new ArrayList<>();
        String[] s = td.text().trim().split(" ");
        for (int i = 0; i < s.length; i++) {
            try {
                if (s[i].contains("{") && s[i].contains("}")) {
                    Course course = new Course();
                    course.setName(s[i - 1]);
                    course.setTeacher(s[i + 1]);
                    if (s.length == i + 3 || s.length > i + 3 && !s[i + 3].contains("{") && !s[i + 2].matches("[0-9]+年.*")) {
                        course.setAddress(s[i + 2]);
                    } else {
                        course.setAddress("");
                    }
                    parseTime(course, s[i], help);
                    course.setAccount(account);
                    course.setId((long) course.hashCode());
                    list.add(course);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * flag标记课程位置，用于定位
     */
    private void mark(Course course) {
        for (int i = 0; i < course.getNodeCnt(); i++) {
            locFlag[i + course.getBeginNode() - 1][course.getWeekday() - 2] = true;
        }
    }

    private void parseTime(Course course, String text, Help help) {
        //beginNode, nodeNum
        Matcher m1 = Pattern.compile("第.*节").matcher(text);
        if (m1.find() && !m1.group().contains("-")) {
            List<Integer> intList = RegexUtils.getNumbers(m1.group());
            course.setBeginNode(intList.get(0));
            course.setNodeCnt(intList.size());
        } else {
            course.setBeginNode(help.beginNode);
            Matcher m2 = Pattern.compile("[0-9]+节\\\\周").matcher(text);
            if (m2.find()) {
                course.setNodeCnt(RegexUtils.getNumbers(m2.group()).get(0));
            } else {
                course.setNodeCnt(help.nodeNum);
            }
        }

        //weekdayCN
        boolean flag = false;
        for (int i = 0; i < 7; i++) {
            if (text.contains(weekdayCN[i])) {
                course.setWeekday(weekday[i]);
                flag = true;
                break;
            }
        }
        if (!flag) {
            int weekday;
            switch (help.col) {
                case 0:
                    weekday = Calendar.MONDAY;
                    break;
                case 1:
                    weekday = Calendar.TUESDAY;
                    break;
                case 2:
                    weekday = Calendar.WEDNESDAY;
                    break;
                case 3:
                    weekday = Calendar.THURSDAY;
                    break;
                case 4:
                    weekday = Calendar.FRIDAY;
                    break;
                case 5:
                    weekday = Calendar.SATURDAY;
                    break;
                case 6:
                    weekday = Calendar.SUNDAY;
                    break;
                default:
                    weekday = 0;
            }
            course.setWeekday(weekday);
        }

        Matcher m2 = Pattern.compile("第[0-9]+-[0-9]+周").matcher(text);
        if (m2.find()) {
            List<Integer> intList = RegexUtils.getNumbers(m2.group());
            int beginWeek = intList.get(0);
            int endWeek = intList.get(1);
            //weekType
            if (text.contains("单周")) {
                beginWeek = beginWeek % 2 == 1 ? beginWeek : beginWeek + 1;
                for (int i = beginWeek; i <= endWeek; i += 2) {
                    course.getWeekSet().add(i);
                }
            } else if (text.contains("双周")) {
                beginWeek = beginWeek % 2 == 0 ? beginWeek : beginWeek + 1;
                for (int i = beginWeek; i <= endWeek; i += 2) {
                    course.getWeekSet().add(i);
                }
            } else {
                for (int i = beginWeek; i <= endWeek; i++) {
                    course.getWeekSet().add(i);
                }
            }
        }
    }

    @Override
    public ObservableSource<List<Course>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> {
            String html = responseBody.string();
            if (html.contains("请登录")) {
                Log.d("Syllabus", "需要重新登录");
                throw new NoAuthException();
            }
            return parse(html);
        });
    }

    private class Help {
        int beginNode;
        int nodeNum;
        int col;
    }

}
