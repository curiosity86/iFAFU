package cn.ifafu.ifafu.mvp.elec_main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.ViewModelProvider
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.ElecMainActivityBinding
import cn.ifafu.ifafu.mvp.elec_login.ElecLoginActivity
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.elec_main_activity.*

class ElecMainActivity : BaseActivity<ElecMainActivityBinding, ElecMainViewModel>(), View.OnClickListener {

    private val optionPicker: OptionsPickerView<String> by lazy {
        OptionsPickerBuilder(this) { options1, options2, _, _ ->
            mViewModel.onSelectBuilding(options1, options2) {
                mBinding.building = it
            }
        }
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择校区")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build<String>()
    }

    override val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("查询中")
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.elec_main_activity
    }

    override fun getViewModel(): ElecMainViewModel? {
        return ViewModelProvider(this)[ElecMainViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_elec)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mViewModel.init(setPickerData = { first, second ->
            optionPicker.setPicker(first, second)
        }, setPickerOptions = { first, second, building, room ->
            optionPicker.setSelectOptions(first, second)
            mBinding.building = building
            mBinding.room = room
        }, showElecBalance = {
           mBinding.feeInfo = it
        }, showCardBalance = {
            mBinding.balance = it
        })

        btn_balance.setOnClickListener(this)
        layout_building.setOnClickListener(this)
        tv_fee.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_balance -> mViewModel.queryCardBalance {
                mBinding.balance = it
            }
            R.id.layout_building -> optionPicker.show()
            R.id.tv_fee -> mViewModel.queryElecBalance(mBinding.room ?: "") {
                mBinding.feeInfo = it
            }
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {//点击editText控件外部
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                if (imm != null) {
                    et_room.clearFocus()
                }
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return window.superDispatchTouchEvent(ev) || onTouchEvent(ev)
    }


    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    override suspend fun startLoginActivity() {
        startActivityForResult(Intent(this, ElecLoginActivity::class.java), Constant.ACTIVITY_LOGIN)
        finish()
    }
}
