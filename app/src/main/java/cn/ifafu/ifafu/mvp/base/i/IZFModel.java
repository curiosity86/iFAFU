package cn.ifafu.ifafu.mvp.base.i;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException;
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
     *
     * @return
     * @throws LoginInfoErrorException 登录信息错误（账号错误，密码错误）
     */
    Observable<Response<String>> reLogin();

}
