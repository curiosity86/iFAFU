package cn.ifafu.ifafu.data.http.elec;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Arrays;

import cn.ifafu.ifafu.data.local.Repository;
import cn.ifafu.ifafu.data.local.RepositoryImpl;
import cn.ifafu.ifafu.data.entity.ElecCookie;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CookieInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        Repository repository = RepositoryImpl.getInstance();
        ElecCookie cookie = repository.getElecCookie();
        if (cookie != null) {
            builder.addHeader("Cookie", cookie.toCookieString());
        }
        Response response = chain.proceed(builder.build());
        if (!response.headers("Set-Cookie").isEmpty()) {
            if (cookie == null) {
                cookie = new ElecCookie();
                cookie.setAccount(repository.getLoginUser().getAccount());
            }
            for (String header: response.headers("Set-Cookie")) {
                String[] kv = header.substring(0, header.indexOf(";")).split("=");
                System.out.println("Cookie put: " + Arrays.toString(kv) + ", Header: " + header);
                cookie.set(kv[0], kv[1]);
            }
            repository.saveElecCookie(cookie);
        }
        return response;
    }
}
