package cn.ifafu.ifafu

import io.reactivex.Observable
import org.junit.Test

class ObservableTest {
    @Test
    fun test() {
        Observable.create<Int> {
            for (i in 1 until 10) {
                println("onNext -- $i")
                it.onNext(i)
                Thread.sleep(1000)
            }
            it.onComplete()
        }
                .doOnSubscribe { println("doOnSubscribe") }
                .doOnEach { println("onEach") }
                .doFinally { println("doFinally") }
                .subscribe {
                    println(it)
                }
        while (true) {

        }
    }
}
