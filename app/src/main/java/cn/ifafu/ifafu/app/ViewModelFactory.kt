package cn.ifafu.ifafu.app

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cn.ifafu.ifafu.ui.login.LoginViewModel
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.syllabus.SyllabusViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = with(modelClass) {
        return modelClass.constructors[0].newInstance(application) as T
    }
}

@Suppress("UNCHECKED_CAST")
class VMProvider(owner: ViewModelStoreOwner): ViewModelProvider(owner, ViewModelFactory) {

    companion object {
        fun init(application: Application) {
            ViewModelFactory.mApplication = application
        }
    }

    private object ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

        internal lateinit var mApplication: Application

        override fun <T : ViewModel> create(modelClass: Class<T>): T  = with(modelClass){
            try {
                return modelClass.constructors[0].newInstance(mApplication) as T
            } catch (e: Exception) {
                throw IllegalArgumentException("Unknown ViewModel class: $name")
            }
        }

    }
}

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory(this.application)
}

fun Fragment.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory(requireActivity().application)
}


