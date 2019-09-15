package cn.ifafu.ifafu.data.entity;

public class ZFApi {

    private String api;

    private String gnmkdm;

    public ZFApi(String api, String gnmkdm) {
        this.api = api;
        this.gnmkdm = gnmkdm;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getGnmkdm() {
        return gnmkdm;
    }

    public void setGnmkdm(String gnmkdm) {
        this.gnmkdm = gnmkdm;
    }
}
