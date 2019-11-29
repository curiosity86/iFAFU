package cn.ifafu.ifafu.mvp.presenter

import android.content.Intent
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.base.addDisposable
import cn.ifafu.ifafu.data.entity.CommentItem
import cn.ifafu.ifafu.data.entity.Response
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.CommentParser2
import cn.ifafu.ifafu.mvp.contract.CommentContract
import cn.ifafu.ifafu.mvp.model.CommentModel
import cn.ifafu.ifafu.mvp.web.WebActivity
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.rxkotlin.toObservable
import java.net.URLDecoder
import java.net.URLEncoder

class CommentPresenter(view: CommentContract.View) : BasePresenter<CommentContract.View, CommentContract.Model>(view, CommentModel(view.context)), CommentContract.Presenter {

    private var response: Response<List<CommentItem>>? = null

    private var flag = false //是否评测完

    private var first = true

    override fun onCreate() {
        super.onCreate()
        if (mModel.getSchoolCode() == School.FAFU_JS) {
            mView.showMessage("金山学院的一键评教预计1号前完工~")
        }
        init()
    }

    override fun click(item: CommentItem) {
        val intent = Intent(mView.context, WebActivity::class.java)
        mModel.getJumpInfo(item).forEach { (k, u) ->
            intent.putExtra(k, u)
        }
        mView.openActivity(intent)
    }

    override fun oneButton() {
        when {
            response == null -> return
            flag -> { //所有老师都评测完，提交评教
                addDisposable {
                    mModel.submit(response!!.hiddenParams)
                            .compose(RxUtils.ioToMain())
                            .subscribe({
                                if (it) {
                                    mView.showMessage("提交评教成功")
                                    mView.killSelf()
                                } else {
                                    mView.showMessage("提交评教失败，请前往网页模式提交评教~")
                                }
                            }, this::onError)
                }
                return
            }
            else -> { //评教每个老师
                mView.setLoadingText("一键评教中")
                addDisposable {
                    response!!.body.toObservable()
                            .flatMap { mModel.commentTeacher(it) }
                            .compose(RxUtils.ioToMain())
                            .doOnSubscribe { mView.showLoading() }
                            .doFinally {
                                init()
                                mView.hideLoading()
                            }
                            .subscribe({
                            }, this::onError)
                }
            }
        }
    }

    private fun init() {
        addDisposable {
            mModel.getCommentList()
                    .compose(RxUtils.ioToMain())
                    .compose(showHideLoading())
                    .subscribe({
                        if (it.code == Response.FAILURE) {
                            mView.showMessage(it.message)
                            mView.killSelf()
                        } else {
                            response = it
                            mView.setRvData(it.body)
                            //检查是否所有老师都评教完
                            flag = true
                            for (item in it.body) {
                                if (!item.isDone) {
                                    flag = false
                                    break
                                }
                            }
                            if (flag) {
                                if (!first) {
                                    mView.showMessage("一键评教完成")
                                    mView.showSuccessTip()
                                } else {
                                    first = false
                                }
                                mView.setButtonText("提交最终评教")
                            }
                        }
                    }, this::onError)
        }
    }
}
