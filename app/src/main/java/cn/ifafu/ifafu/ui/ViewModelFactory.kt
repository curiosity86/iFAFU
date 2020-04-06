package cn.ifafu.ifafu.ui

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.experiment.ui.score.filter.ScoreFilterViewModel
import cn.ifafu.ifafu.experiment.ui.score.list.ScoreListViewModel
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.main.old_theme.MainOldViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    private val repo = RepositoryImpl

    override fun <T : ViewModel> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application)
            isAssignableFrom(MainOldViewModel::class.java) -> MainOldViewModel(repo)
            isAssignableFrom(cn.ifafu.ifafu.experiment.ui.main.old.MainOldViewModel::class.java) -> cn.ifafu.ifafu.experiment.ui.main.old.MainOldViewModel(repo)
            isAssignableFrom(ScoreListViewModel::class.java) -> ScoreListViewModel(application)
            isAssignableFrom(ScoreFilterViewModel::class.java) -> ScoreFilterViewModel()
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


