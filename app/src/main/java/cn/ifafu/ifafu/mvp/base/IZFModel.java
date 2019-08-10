package cn.ifafu.ifafu.mvp.base;

import android.os.NetworkOnMainThreadException;

import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import io.reactivex.Observable;

public interface IZFModel extends IModel {

    /**
     * 登录
     *
     * @param user user
     * @return {@link Response#SUCCESS} 登录成功    body = user only with name
     *         {@link Response#FAILURE} 信息错误    msg = return msg
     *         {@link Response#ERROR}   服务器错误  msg = error msg
     */
    Observable<Response<String>> login(User user);

    /**
     * 检查用户token的有效性，需在子线程中调用
     *
     * @return token有效性
     * @throws NetworkOnMainThreadException
     */
    Observable<Boolean> isTokenAlive(User user);

    User getUser();
}
