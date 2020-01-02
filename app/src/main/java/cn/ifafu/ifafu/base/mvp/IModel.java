package cn.ifafu.ifafu.base.mvp;

import cn.ifafu.ifafu.entity.Response;
import cn.ifafu.ifafu.entity.exception.LoginInfoErrorException;
import io.reactivex.Observable;

public interface IModel {

    void onDestroy();

    /**
     * 重新登录
     * @return {@link Response#SUCCESS} 登录成功    body = user only with name
     *         {@link Response#FAILURE} 信息错误    msg = return msg
     *         {@link Response#ERROR}   服务器错误  msg = error msg
     * @throws LoginInfoErrorException 登录信息错误（账号错误，密码错误）
     */
    Observable<Response<String>> reLogin();
}
