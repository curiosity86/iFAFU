package cn.ifafu.ifafu.ui.feedback

import android.os.Bundle
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.FeedbackActivityBinding

class FeedbackActivity : BaseActivity<FeedbackActivityBinding, FeedbackViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.feedback_activity;
    }

    override fun getViewModel(): FeedbackViewModel? {
        return VMProvider(this)[FeedbackViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.btnSubmit.setOnClickListener {
            mViewModel.submit(mBinding.contact.text.toString(), mBinding.message.text.toString())
        }
    }

}