package cn.ifafu.ifafu.data.entity;

import java.util.List;

/**
 * 学年和学期选项
 * Created by woolsen on 19/9/18
 */
public class YearTerm {

    private List<String> yearList;
    private List<String> termList;

    public YearTerm(List<String> yearList, List<String> termList) {
        this.yearList = yearList;
        this.termList = termList;
    }

    public List<String> getYearList() {
        return yearList;
    }

    public void setYearList(List<String> yearList) {
        this.yearList = yearList;
    }

    public List<String> getTermList() {
        return termList;
    }

    public void setTermList(List<String> termList) {
        this.termList = termList;
    }

    public void addYear(String year) {
        yearList.add(year);
    }

    public void addYear(int index, String year) {
        yearList.add(index, year);
    }

    public void addTerm(String term) {
        termList.add(term);
    }

    public void addTerm(int index, String term) {
        termList.add(index, term);
    }

    public int yearIndexOf(String year) {
        return yearList.indexOf(year);
    }

    public int termIndexOf(String term) {
        return termList.indexOf(term);
    }

    public String getYear(int index) {
        return yearList.get(index);
    }

    public String getTerm(int index) {
        return termList.get(index);
    }
}
