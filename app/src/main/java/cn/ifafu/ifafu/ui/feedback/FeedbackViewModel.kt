package cn.ifafu.ifafu.ui.feedback

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedbackViewModel(application: Application) : BaseViewModel(application) {

    val contact = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val submitCommand = View.OnClickListener { submit() }

    private val repo = RepositoryImpl

    private fun submit() = GlobalScope.launch {
        val message = message.value
        if (message.isNullOrBlank()) {
            toast("请输入反馈内容")
            return@launch
        }
        val contact = contact.value
        if (contact.isNullOrBlank()) {
            toast("请输入联系方式")
            return@launch
        }
        repo.postFeedback(message, contact).getOrFailure {
            toast(it.errorMessage())
        } ?.let {
            toast(it)
        }
    }
}