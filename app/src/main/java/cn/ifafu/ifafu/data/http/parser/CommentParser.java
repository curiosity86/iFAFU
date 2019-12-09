package cn.ifafu.ifafu.data.http.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ifafu.ifafu.data.entity.CommentItem;
import cn.ifafu.ifafu.data.entity.Response;

public class CommentParser extends BaseParser<Response<List<CommentItem>>> {

    @Override
    public Response<List<CommentItem>> parse(@NotNull String html) throws Exception {
        Document document = Jsoup.parse(html);
        Elements table = document.select("table[id=\"Datagrid1\"]"); //定位到表格
        if (html.contains("您已经评价过！")) {
            return Response.failure("您已经评价过！");
        } else if (table.size() == 0) {
            return Response.failure("评教系统暂未开放！");
        }
        Elements lines = table.get(0).getElementsByTag("tr");
        List<CommentItem> list = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            Elements blocks = lines.get(i).getElementsByTag("td");
            //获取课程名字
            String courseName = blocks.get(0).text();
            //获取老师和评教地址
            Elements as = blocks.get(1).getElementsByTag("a");
            for (Element a : as) {
                CommentItem item = new CommentItem();
                Matcher urlMatcher = Pattern.compile("open\\(.*ll'").matcher(a.outerHtml());
                if (urlMatcher.find()) {
                    item.setCourseName(courseName);
                    String aText = a.text();
                    item.setDone(aText.contains("已评价"));
                    item.setTeacherName(aText.replaceAll("\\(.*\\)", ""));
                    String matchUrl = urlMatcher.group()
                            .replaceAll("&amp;", "&");
                    item.setCommentUrl(matchUrl.substring(6, matchUrl.length() - 1));
                    list.add(item);
                }
            }
        }
        Response<List<CommentItem>> response = new Response<>();
        ParamsParser paramsParser = new ParamsParser();
        response.setBody(list);
        response.setHiddenParams(paramsParser.parse(html));
        return response;
    }
}
