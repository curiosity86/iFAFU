package cn.ifafu.ifafu.mvp.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.CommentItem
import cn.ifafu.ifafu.mvp.contract.CommentContract
import cn.ifafu.ifafu.mvp.presenter.CommentPresenter
import cn.ifafu.ifafu.util.SPUtils
import cn.ifafu.ifafu.view.adapter.CommentRvAdapter
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : BaseActivity<CommentContract.Presenter>(), CommentContract.View {
    private lateinit var progressDialog: ProgressDialog

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_comment
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(R.id.tb_comment)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mPresenter = CommentPresenter(this)
        progressDialog = ProgressDialog(this)
        btn_one.setOnClickListener {
            if (btn_one.text.contains("提交最终评教")) {
                MaterialDialog(this).show {
                    message(R.string.comment_1)
                    positiveButton(text = "同意") {
                        mPresenter.oneButton()
                    }
                    negativeButton(text = "拒绝") {
                        this@CommentActivity.finish()
                    }
                }
            } else {
                mPresenter.oneButton()
            }
        }
        if (!SPUtils.get("FIRST").contain("comment_agree")) {
            SPUtils.get("FIRST").putBoolean("comment_agree", true)
            MaterialDialog(this).show {
                message(R.string.comment_1)
                positiveButton(text = "同意")
                negativeButton(text = "拒绝") {
                    this@CommentActivity.finish()
                }
            }
        }
    }

    override fun setRvData(list: List<CommentItem>) {
        rv_comment.layoutManager = LinearLayoutManager(this)
        rv_comment.adapter = CommentRvAdapter(list) {
            mPresenter.click(it)
        }
    }

    override fun setLoadingText(text: String) {
        progressDialog.setText(text)
    }

    override fun showLoading() {
        progressDialog.show()
    }

    override fun hideLoading() {
        progressDialog.cancel()
    }

    override fun setButtonText(text: String) {
        btn_one.text = text
    }

    override fun showSuccessTip() {
        MaterialDialog(this).show {
            message(R.string.comment_2)
            positiveButton(text = "收到")
        }
    }

}