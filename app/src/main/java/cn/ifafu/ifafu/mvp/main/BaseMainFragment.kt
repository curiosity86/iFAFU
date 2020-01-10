package cn.ifafu.ifafu.mvp.main

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.mvp.BaseFragment
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.view.adapter.AccountAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.alibaba.fastjson.JSONObject
import kotlinx.android.synthetic.main.account_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseMainFragment<T : BaseMainContract.Presenter> : BaseFragment<T>(), BaseMainContract.View {


    private var mAccountAdapter: AccountAdapter? = null

    private val checkoutDialog: MaterialDialog by lazy {
        MaterialDialog(requireContext()).apply {
            customView(viewRes = R.layout.account_dialog)
            title(text = "多账号管理")
            negativeButton(text = "添加账号") {
                openLoginActivity()
            }
            if (BuildConfig.DEBUG) {
                neutralButton(text = "导入账号") {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val data = cm.primaryClip
                            val item = data!!.getItemAt(0)
                            val content = item.text.toString()
                            val list = JSONObject.parseArray(content, User::class.java)
                            list.forEach {
                                Repository.saveUser(it)
                            }
                            withContext(Dispatchers.Main) {
                                mAccountAdapter?.replaceData(list)
                            }
                            showMessage("导入成功")
                        } catch (e: Exception) {
                            showMessage("导入失败")
                        }
                    }
                }
            }
        }
    }

    override fun setCheckoutDialogData(users: List<User>) {
        if (BuildConfig.DEBUG) {
            Log.d("User", JSONObject.toJSONString(users))
        }
        if (mAccountAdapter == null) {
            mAccountAdapter = AccountAdapter(users) {
                showAccountDetail(it)
            }
            val rvAccount = checkoutDialog.getCustomView().rv_account
            rvAccount.adapter = mAccountAdapter
            rvAccount.layoutManager = LinearLayoutManager(context)
        } else {
            mAccountAdapter?.replaceData(users)
        }
    }

    override fun showCheckoutDialog() {
        checkoutDialog.show()
    }

    override fun hideCheckoutDialog() {
        checkoutDialog.hide()
    }

    private fun openLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("from", Constant.ACTIVITY_MAIN)
        startActivityForResult(intent, Constant.ACTIVITY_LOGIN)
    }

    private fun showAccountDetail(user: User) {
        MaterialDialog(context!!).show {
            title(text = "${user.name} ${user.account}")
            customView(viewRes = R.layout.account_password_dialog)
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
