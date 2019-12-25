package cn.ifafu.ifafu.mvp.elec_login

import android.content.Intent
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.data.RepositoryImpl
import cn.ifafu.ifafu.entity.ElecCookie
import cn.ifafu.ifafu.entity.ElecQuery
import cn.ifafu.ifafu.entity.ElecUser
import cn.ifafu.ifafu.mvp.elec_main.ElecMainActivity
import cn.ifafu.ifafu.util.RxUtils
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ElecLoginPresenter internal constructor(view: ElecLoginContract.View) : BasePresenter<ElecLoginContract.View, ElecLoginContract.Model>(view, ElecLoginModel(view.context)), ElecLoginContract.Presenter {

    private lateinit var elecUser: ElecUser

    override fun onCreate() {
        GlobalScope.launch(Dispatchers.IO) {
            elecUser = mModel.getUser().run {
                if (this == null) {
                    val elecUser = ElecUser()
                    val user = RepositoryImpl.getInUseUser()
                    elecUser.account = user!!.account
                    val account = user.account
                    if (user.schoolCode == School.FAFU_JS) {
                        elecUser.xfbAccount = "0$account"
                    } else {
                        elecUser.xfbAccount = account
                    }
                    elecUser
                } else {
                    this
                }
            }
            launch(Dispatchers.Main) {
                mView.setPasswordText(elecUser.password)
                mView.setSnoEtText(elecUser.xfbAccount)
                verify()
            }
        }
    }

    override fun verify() {
        val d = mModel.verifyBitmap()
                .compose(RxUtils.ioToMain())
                .subscribe({ bitmap -> mView.setVerifyBitmap(bitmap) }, { this.onError(it) })
        mCompDisposable.add(d)
    }

    override fun login() {
        val sno = mView.getSnoText()
        val password = mView.getPasswordText()
        val verify = mView.getVerifyText()
        val d = mModel.login(sno, password, verify)
                .doOnNext { s ->
                    val jo = JSONObject.parseObject(s)
                    if (jo.getBoolean("IsSucceed") == true) {
                        val obj2 = jo.getJSONObject("Obj2")

                        elecUser.xfbAccount = sno
                        elecUser.name = obj2.getString("NAME")
                        elecUser.password = password
                        mModel.save(elecUser)

                        val elecCookie = ElecCookie()
                        elecCookie.account = elecUser.account
                        elecCookie.rescouseType = obj2.getString("RescouseType")
                        mModel.save(elecCookie)

                        val elecQuery = ElecQuery()
                        elecQuery.account = elecUser.account
                        elecQuery.xfbId = obj2.getString("ACCOUNT")
                        mModel.save(elecQuery)
                    }
                }
                .compose(RxUtils.ioToMain())
                .compose(showHideLoading())
                .subscribe({ s ->
                    val jo = JSONObject.parseObject(s)
                    if (jo.getBoolean("IsSucceed")) {
                        mView.showMessage("登录成功")
                        mView.openActivity(Intent(mView.context, ElecMainActivity::class.java))
                        mView.killSelf()
                    } else {
                        if (jo.containsKey("Msg")) {
                            mView.showMessage(jo.getString("Msg"))
                        }
                        verify()
                    }
                }, { throwable ->
                    onError(throwable)
                    verify()
                })
        mCompDisposable.add(d)
    }

}
