package cn.ifafu.ifafu;

import org.junit.Test;

import java.text.ParseException;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.http.parser.SyllabusParser;

public class UnitTest {

    @Test
    public void test() {
        String html = "\n" +
                "\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<HTML lang=\"gb2312\">\n" +
                "\t<HEAD>\n" +
                "\t\t<title>现代教学管理信息系统</title><meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\">\n" +
                "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\n" +
                "\t\t<meta http-equiv=\"Content-Language\" content=\"gb2312\">\n" +
                "\t\t<meta content=\"all\" name=\"robots\">\n" +
                "\t\t<meta content=\"作者信息\" name=\"author\">\n" +
                "\t\t<meta content=\"版权信息\" name=\"Copyright\">\n" +
                "\t\t<meta content=\"站点介绍\" name=\"description\">\n" +
                "\t\t<meta content=\"站点关键词\" name=\"keywords\">\n" +
                "\t\t<LINK href=\"style/base/favicon.ico\" type=\"image/x-icon\" rel=\"icon\">\n" +
                "\t\t\t<LINK media=\"all\" href=\"style/base/jw.css\" type=\"text/css\" rel=\"stylesheet\">\n" +
                "\t\t\t\t<LINK media=\"all\" href=\"style/standard/jw.css\" type=\"text/css\" rel=\"stylesheet\">\n" +
                "\t\t\t\t\t<!--<script defer>   \n" +
                "\t\tfunction  PutSettings()   \n" +
                "\t\t\t{     \n" +
                "\t\t\tfactory.printing.header=\"\";   \n" +
                "\t\t\tfactory.printing.footer=\"\";   \n" +
                "\t\t    factory.printing.leftMargin=\"5\";   \n" +
                "\t\t\tfactory.printing.topMargin=\"5\";   \n" +
                "\t\t\tfactory.printing.rightMargin=\"5\";   \n" +
                "\t\t\tfactory.printing.bottomMargin=\"5\";\n" +
                "\t\t }\n" +
                "\n" +
                "\t\t\t\t\t</script>-->\n" +
                "\t\t\t\t\t<style> @media Print { .bgnoprint { }\n" +
                "\t.noprint { DISPLAY: none }}\n" +
                "\t</style>\n" +
                "\t</HEAD>\n" +
                "\t<BODY>\n" +
                "\t\t<!--<OBJECT id=\"factory\" style=\"DISPLAY: none\" codeBase=\"ScriptX.cab#Version=5,60,0,360\" classid=\"clsid:1663ed61-23eb-11d2-b92f-008048fdd814\"\n" +
                "\t\t\tVIEWASTEXT>\n" +
                "\t\t</OBJECT>-->\n" +
                "\n" +
                "<input type=\"hidden\" name=\"__VIEWSTATEGENERATOR\" value=\"55530A43\" />\n" +
                "\n" +
                "<script language=\"javascript\" type=\"text/javascript\">\n" +
                "<!--\n" +
                "\tfunction __doPostBack(eventTarget, eventArgument) {\n" +
                "\t\tvar theform;\n" +
                "\t\tif (window.navigator.appName.toLowerCase().indexOf(\"microsoft\") > -1) {\n" +
                "\t\t\ttheform = document.xskb_form;\n" +
                "\t\t}\n" +
                "\t\telse {\n" +
                "\t\t\ttheform = document.forms[\"xskb_form\"];\n" +
                "\t\t}\n" +
                "\t\ttheform.__EVENTTARGET.value = eventTarget.split(\"$\").join(\":\");\n" +
                "\t\ttheform.__EVENTARGUMENT.value = eventArgument;\n" +
                "\t\ttheform.submit();\n" +
                "\t}\n" +
                "// -->\n" +
                "</script>\n" +
                "\n" +
                "\t\t\t<!-- 多功能操作区 -->\n" +
                "\t\t\t<!-- 内容显示区开始 -->\n" +
                "\t\t\t<div class=\"main_box \">\n" +
                "\t\t\t\t<div class=\"mid_box\">\n" +
                "\t\t\t\t\t<div class=\"title noprint\">\n" +
                "\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t<!-- 查询得到的数据量显示区域 --></p>\n" +
                "\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t<!-- From内容 --><span class=\"formbox\">\n" +
                "\t\t\t\t\t\t<TABLE class=\"formlist noprint\" id=\"Table2\" width=\"100%\">\n" +
                "\t\t\t\t\t\t\t<TR>\n" +
                "\t\t\t\t\t\t\t\t<TD align=\"center\"><select name=\"xnd\" onchange=\"__doPostBack('xnd','')\" language=\"javascript\" id=\"xnd\">\n" +
                "\t<option selected=\"selected\" value=\"2019-2020\">2019-2020</option>\n" +
                "\t<option value=\"2018-2019\">2018-2019</option>\n" +
                "\n" +
                "</select><span id=\"Label2\"><font size=\"4\">学年第</font></span><select name=\"xqd\" onchange=\"__doPostBack('xqd','')\" language=\"javascript\" id=\"xqd\">\n" +
                "\t<option selected=\"selected\" value=\"1\">1</option>\n" +
                "\t<option value=\"2\">2</option>\n" +
                "\t<option value=\"3\">3</option>\n" +
                "\n" +
                "</select><span id=\"Label1\"><font size=\"4\">学期学生个人课程表</font></span></TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t\t<TR class=\"trbg1\">\n" +
                "\t\t\t\t\t\t\t\t<TD><span id=\"Label5\">学号：3186020002</span>|\n" +
                "\t\t\t\t\t\t\t\t\t<span id=\"Label6\">姓名：林晓强</span>|\n" +
                "\t\t\t\t\t\t\t\t\t<span id=\"Label7\">学院：计算机与信息学院</span>|\n" +
                "\t\t\t\t\t\t\t\t\t<span id=\"Label8\">专业：网络工程</span>|\n" +
                "\t\t\t\t\t\t\t\t\t<span id=\"Label9\">行政班：18网工1班</span>\n" +
                "\t\t\t\t\t\t\t\t\t&nbsp;&nbsp;&nbsp;&nbsp;<span id=\"labTS\"><font color=\"Red\"></font></span><span id=\"labTip\"><font color=\"Red\"></font></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input name=\"btnPrint\" id=\"btnPrint\" type=\"button\" class=\"button\" style=\"DISPLAY:none\" onclick=\"window.print();\" value=\"打印课表\" />\n" +
                "\t\t\t\t\t\t\t\t</TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t</TABLE>\n" +
                "\t\t\t\t\t\t<br>\n" +
                "\t\t\t\t\t\t<table id=\"Table1\" class=\"blacktab\" bordercolor=\"Black\" border=\"0\" width=\"100%\">\n" +
                "\t<tr>\n" +
                "\t\t<td colspan=\"2\" rowspan=\"1\" width=\"2%\">时间</td><td align=\"Center\" width=\"14%\">星期一</td><td align=\"Center\" width=\"14%\">星期二</td><td align=\"Center\" width=\"14%\">星期三</td><td align=\"Center\" width=\"14%\">星期四</td><td align=\"Center\" width=\"14%\">星期五</td><td class=\"noprint\" align=\"Center\" width=\"14%\">星期六</td><td class=\"noprint\" align=\"Center\" width=\"14%\">星期日</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td colspan=\"2\">早晨</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td rowspan=\"5\" width=\"1%\">上午</td><td width=\"1%\">第1节</td><td align=\"Center\" rowspan=\"2\" width=\"7%\">概率论与数理统计<br>周一第1,2节{第6-17周}<br>沈群<br>创102</td><td align=\"Center\" rowspan=\"2\" width=\"7%\">英语4<br>周二第1,2节{第3-15周|单周}<br>许明欣<br>创114<br><br>英语4<br>周二第1,2节{第4-16周|双周}<br>许明欣<br>诚C203</td><td align=\"Center\" rowspan=\"2\" width=\"7%\">概率论与数理统计<br>周三第1,2节{第6-17周}<br>沈群<br>创102</td><td align=\"Center\" rowspan=\"2\" width=\"7%\">计算机网络基本原理<br>周四第1,2节{第2-13周}<br>蒋萌辉(蒋萌辉)<br>诚B104</td><td align=\"Center\" rowspan=\"2\" width=\"7%\">体育3(太极运动)<br>{第1-14周|2节/周}<br>毛丹丹<br>校田径场</td><td class=\"noprint\" align=\"Center\" width=\"7%\">&nbsp;</td><td class=\"noprint\" align=\"Center\" width=\"7%\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第2节</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第3节</td><td align=\"Center\" rowspan=\"2\">计算机网络基本原理<br>周一第3,4节{第6-13周}<br>蒋萌辉(蒋萌辉)<br>田C305（机房）</td><td align=\"Center\" rowspan=\"2\">毛泽东思想和中国特色社会主义理论体系概论<br>周二第3,4节{第1-15周}<br>王仪祥<br>田307</td><td align=\"Center\" rowspan=\"2\">计算机组成原理<br>周三第3,4节{第2-10周}<br>朱仕浪(朱仕浪)<br>田409</td><td align=\"Center\" rowspan=\"2\">英语4<br>周四第3,4节{第3-16周}<br>许明欣<br>创114</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第4节</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第5节</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td rowspan=\"4\">下午</td><td>第6节</td><td align=\"Center\" rowspan=\"2\">计算机组成原理<br>周一第6,7节{第2-10周}<br>朱仕浪(朱仕浪)<br>田105</td><td align=\"Center\" rowspan=\"2\">大学物理实验D<br>周二第6,7节{第2-9周}<br>冯利<br></td><td align=\"Center\" rowspan=\"2\">毛泽东思想和中国特色社会主义理论体系概论<br>周三第6,7节{第1-15周}<br>王仪祥<br>田307</td><td align=\"Center\">&nbsp;</td><td align=\"Center\" rowspan=\"2\">大学生心理健康教育<br>周五第6,7节{第1-6周}<br>张露<br>创111</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第7节</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第8节</td><td align=\"Center\" rowspan=\"2\">计算机组成原理<br>周一第8,9节{第6-9周}<br>朱仕浪(朱仕浪)<br>田C505（硬件实验室）</td><td align=\"Center\" rowspan=\"2\">计算机网络基本原理<br>周二第8,9节{第2-13周}<br>蒋萌辉(蒋萌辉)<br>诚B104</td><td align=\"Center\">毛泽东思想和中国特色社会主义理论体系概论<br>周三第8节{第1-15周}<br>王仪祥<br>田307</td><td align=\"Center\">&nbsp;</td><td align=\"Center\" rowspan=\"2\">形势与政策3<br>周五第8,9节{第3-12周}<br>陈达/王晓<br>田206</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第9节</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td rowspan=\"3\">晚上</td><td>第10节</td><td align=\"Center\" rowspan=\"3\">创新创业基础<br>周一第10,11,12节{第1-1周|单周}<br>孙海岗/施木田/陈秀秀/赖宇芳/林榅荷/吴元民/刘树人<br>创203<br><br>创新创业基础<br>周一第10,11,12节{第3-4周}<br>孙海岗/施木田/陈秀秀/赖宇芳/林榅荷/吴元民/刘树人<br>创203<br><br>创新创业基础<br>周一第10,11,12节{第6-9周}<br>孙海岗/施木田/陈秀秀/赖宇芳/林榅荷/吴元民/刘树人<br>创203</td><td align=\"Center\" rowspan=\"3\">书法的临摹与创作<br>{第2-2周|3节/周}<br>沈伟棠<br>创210<br><br>书法的临摹与创作<br>{第4-14周|3节/周}<br>沈伟棠<br>创210</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第11节</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>第12节</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td><td class=\"noprint\" align=\"Center\">&nbsp;</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\t\t\t\t\t\t<br>\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t<div class=\"noprint\" align=\"left\">调、停（补）课信息：</div>\n" +
                "\t\t\t\t\t\t<table class=\"datelist noprint\" cellspacing=\"0\" cellpadding=\"3\" border=\"0\" id=\"DBGrid\" width=\"100%\">\n" +
                "\t<tr class=\"datelisthead\">\n" +
                "\t\t<td>编号</td><td>课程名称</td><td>原上课时间地点教师</td><td>现上课时间地点教师</td><td>申请时间</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\t\t\t\t\t\t<TABLE class=\"noprint\" id=\"Table3\" width=\"100%\">\n" +
                "\t\t\t\t\t\t\t<TR>\n" +
                "\t\t\t\t\t\t\t\t<TD align=\"left\">实践课(或无上课时间)信息：</TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t\t<TR>\n" +
                "\t\t\t\t\t\t\t\t<TD valign=\"top\"><table class=\"datelist\" cellspacing=\"0\" cellpadding=\"3\" border=\"0\" id=\"DataGrid1\" width=\"100%\">\n" +
                "\t<tr class=\"datelisthead\">\n" +
                "\t\t<td>课程名称</td><td>教师</td><td>学分</td><td>起止周</td><td>上课时间</td><td>上课地点</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>思想政治理论课实践教学</td><td>王仪祥</td><td>2.0</td><td>01-30</td><td>&nbsp;</td><td>&nbsp;</td>\n" +
                "\t</tr><tr class=\"alt\">\n" +
                "\t\t<td>心理健康素质拓展</td><td>张露</td><td>0.5</td><td>01-06</td><td>&nbsp;</td><td>&nbsp;</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>英语自主听力（学习）4</td><td>许明欣</td><td>1.0</td><td>03-16</td><td>&nbsp;</td><td>&nbsp;</td>\n" +
                "\t</tr>\n" +
                "</table></TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t\t<TR>\n" +
                "\t\t\t\t\t\t\t\t<TD align=\"left\">实习课信息：</TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t\t<TR>\n" +
                "\t\t\t\t\t\t\t\t<TD><table class=\"datelist\" cellspacing=\"0\" cellpadding=\"3\" border=\"0\" id=\"DBGridYxkc\" width=\"100%\">\n" +
                "\t<tr class=\"datelisthead\">\n" +
                "\t\t<td>学年</td><td>学期</td><td>课程名称</td><td>实习时间</td><td>模块代号</td><td>先修模块</td><td>实习编号</td>\n" +
                "\t</tr>\n" +
                "</table></TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t\t<TR>\n" +
                "\t\t\t\t\t\t\t\t<TD align=\"left\">未安排上课时间的课程：</TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t\t<TR>\n" +
                "\t\t\t\t\t\t\t\t<TD><table class=\"datelist\" cellspacing=\"0\" cellpadding=\"3\" border=\"0\" id=\"Datagrid2\" width=\"100%\">\n" +
                "\t<tr class=\"datelisthead\">\n" +
                "\t\t<td>学年</td><td>学期</td><td>课程名称</td><td>教师姓名</td><td>学分</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>2019-2020</td><td>1</td><td>创业创新领导力</td><td>尔雅通识课1</td><td>2.0</td>\n" +
                "\t</tr><tr class=\"alt\">\n" +
                "\t\t<td>2019-2020</td><td>1</td><td>思想政治理论课实践教学</td><td>王仪祥</td><td>2.0</td>\n" +
                "\t</tr><tr>\n" +
                "\t\t<td>2019-2020</td><td>1</td><td>心理健康素质拓展</td><td>张露</td><td>0.5</td>\n" +
                "\t</tr><tr class=\"alt\">\n" +
                "\t\t<td>2019-2020</td><td>1</td><td>英语自主听力（学习）4</td><td>许明欣</td><td>1.0</td>\n" +
                "\t</tr>\n" +
                "</table></TD>\n" +
                "\t\t\t\t\t\t\t</TR>\n" +
                "\t\t\t\t\t\t</TABLE>\n" +
                "\t\t\t\t\t</span>\n" +
                "\t\t\t\t\t<div class=\"footbox noprint\"><em class=\"footbox_con\"><span class=\"pagination\"></span>\n" +
                "\t\t\t\t\t\t\t<span class=\"footbutton\"></span>\n" +
                "\t\t\t\t\t\t\t<!-- 底部按钮位置 --></em></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</form>\n" +
                "\t</BODY>\n" +
                "</HTML>\n";
        SyllabusParser parser = new SyllabusParser();
        for (Course course : parser.parse(html)) {
            System.out.println(course);
        }
    }


}
