package cn.ifafu.ifafu.app

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.login.LoginViewModel
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.main.old_theme.MainOldViewModel
import cn.ifafu.ifafu.ui.syllabus.SyllabusViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    private val repo = RepositoryImpl

    override fun <T : ViewModel> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application)
            isAssignableFrom(MainOldViewModel::class.java) -> MainOldViewModel(repo)
            else -> modelClass.constructors[0].newInstance(application)
        } as T
    }
}

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory(this.application)
}

fun Fragment.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory(requireActivity().application)
}


