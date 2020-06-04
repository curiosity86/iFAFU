package cn.ifafu.ifafu.ui.comment2

import android.os.Bundle
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.ui.comment.CommentRvAdapter
import cn.ifafu.ifafu.ui.view.LoadingDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_comment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommentActivity2 : BaseActivity() {

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val mAdapter = CommentRvAdapter {
        mViewModel.click(it)
    }

    private val mViewModel by viewModel<CommentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        //沉浸状态栏
        ImmersionBar.with(this)
                .titleBarMarginTop(R.id.tb_comment)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        //检查是否同意过条款
        if (!SPUtils.getInstance().contains("comment_agree")) {
            SPUtils.getInstance().put("comment_agree", true)
            showCommentClause()
        }

        rv_comment.adapter = mAdapter

        btn_commit.setOnClickListener {
            //TODO
        }

        //初始化ViewModel
        mViewModel.list.observe(this, Observer {
            mAdapter.setList(it)
        })
    }

    private fun showCommentClause() {
        MaterialDialog(this).show {
            message(R.string.comment_1)
            positiveButton(text = "同意")
            negativeButton(text = "拒绝") {
                this@CommentActivity2.finish()
            }
        }
    }
}