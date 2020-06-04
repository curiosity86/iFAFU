package cn.ifafu.ifafu.ui.comment2

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.CommentItem

class CommentViewModel(application: Application) : BaseViewModel(application) {

    private val _list = MutableLiveData<List<CommentItem>>()
    val list: LiveData<List<CommentItem>>
        get() = _list

    fun click(it: CommentItem) {

    }

}