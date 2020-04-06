package cn.ifafu.ifafu.util

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalTypeInference

internal const val DEFAULT_TIMEOUT = 5000L

@OptIn(ExperimentalTypeInference::class)
fun <T> mutableLiveData(
        context: CoroutineContext = EmptyCoroutineContext,
        timeoutInMs: Long = DEFAULT_TIMEOUT,
        @BuilderInference block: suspend LiveDataScope<T>.() -> Unit
): MutableLiveData<T> = liveData(context, timeoutInMs, block) as MutableLiveData<T>

