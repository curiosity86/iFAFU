package cn.ifafu.ifafu.data;

import java.util.Map;

public class Api {

    private String baseHost;

    private String token;

    private Map<String, String> apiMap;

    public String getBaseHost() {
        return baseHost;
    }

    public void setBaseHost(String baseHost) {
        this.baseHost = baseHost;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, String> getApiMap() {
        return apiMap;
    }

    public void setApiMap(Map<String, String> apiMap) {
        this.apiMap = apiMap;
    }
}
