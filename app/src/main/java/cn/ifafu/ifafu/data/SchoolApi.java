package cn.ifafu.ifafu.data;

public class SchoolApi {
    private int schoolCode;

    private String base;

    private String syllabusApi;

    private String syllabusZD;

    private String examApi;

    private String examZD;

    private String scoreApi;

    private String scoreZD;

    public SchoolApi(int schoolCode, String base, String syllabusApi, String syllabusZD, String examApi, String examZD, String scoreApi, String scoreZD) {
        this.schoolCode = schoolCode;
        this.base = base;
        this.syllabusApi = syllabusApi;
        this.syllabusZD = syllabusZD;
        this.examApi = examApi;
        this.examZD = examZD;
        this.scoreApi = scoreApi;
        this.scoreZD = scoreZD;
    }

    public int getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(int schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSyllabusApi() {
        return syllabusApi;
    }

    public void setSyllabusApi(String syllabusApi) {
        this.syllabusApi = syllabusApi;
    }

    public String getSyllabusZD() {
        return syllabusZD;
    }

    public void setSyllabusZD(String syllabusZD) {
        this.syllabusZD = syllabusZD;
    }

    public String getExamApi() {
        return examApi;
    }

    public void setExamApi(String examApi) {
        this.examApi = examApi;
    }

    public String getExamZD() {
        return examZD;
    }

    public void setExamZD(String examZD) {
        this.examZD = examZD;
    }

    public String getScoreApi() {
        return scoreApi;
    }

    public void setScoreApi(String scoreApi) {
        this.scoreApi = scoreApi;
    }

    public String getScoreZD() {
        return scoreZD;
    }

    public void setScoreZD(String scoreZD) {
        this.scoreZD = scoreZD;
    }
}
