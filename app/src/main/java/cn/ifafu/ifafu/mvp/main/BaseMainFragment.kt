package cn.ifafu.ifafu.mvp.main

import android.app.Activity
import android.content.Intent
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseFragment
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.view.adapter.AccountAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.dialog_account.view.*

abstract class BaseMainFragment<T : BaseMainContract.Presenter> : BaseFragment<T>(), BaseMainContract.View {


    private var mAccountAdapter: AccountAdapter? = null

    private var checkoutDialog: MaterialDialog? = null

    override fun setCheckoutDialogData(users: List<User>) {
        if (mAccountAdapter == null) {
            checkoutDialog = MaterialDialog(context!!).apply {
                customView(viewRes = R.layout.dialog_account)
                title(text = "多账号管理")
                negativeButton(text = "添加账号") {
                    openLoginActivity()
                }
            }
            mAccountAdapter = AccountAdapter(users) {
                showAccountDetail(it)
            }
            val rvAccount = checkoutDialog!!.getCustomView().rv_account
            rvAccount.adapter = mAccountAdapter
            rvAccount.layoutManager = LinearLayoutManager(context)
        } else {
            mAccountAdapter?.replaceData(users)
        }
    }

    override fun showCheckoutDialog() {
        checkoutDialog?.show()
    }

    override fun hideCheckoutDialog() {
        checkoutDialog?.hide()
    }

    private fun openLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("from", Constant.ACTIVITY_MAIN)
        startActivityForResult(intent, Constant.ACTIVITY_LOGIN)
    }

    private fun showAccountDetail(user: User) {
        MaterialDialog(context!!).show {
            title(text = user.name)
            customView(viewRes = R.layout.dialog_account_detail)
            getCustomView().findViewById<EditText>(R.id.et_password).setText(user.password)
            negativeButton(text = "删除账号") {
                mPresenter.deleteUser(user)
            }
            positiveButton(text = "切换账号") {
                mPresenter.checkoutTo(user)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.ACTIVITY_LOGIN && resultCode == Activity.RESULT_OK) {
            mPresenter.addAccountSuccess()
        } else if (requestCode == Constant.ACTIVITY_SETTING && resultCode == Activity.RESULT_OK) {
            mPresenter.checkoutTheme()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
