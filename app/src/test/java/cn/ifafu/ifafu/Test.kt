package cn.ifafu.ifafu

import cn.ifafu.ifafu.data.new_http.impl.JWServiceImpl
import kotlinx.coroutines.*
import org.junit.Test

class Test {
    data class User(val name: String)

    private fun testCo() = runBlocking {
        GlobalScope.launch(Dispatchers.Main) {
            println("Main:        " + Thread.currentThread().name)
        }
        GlobalScope.launch(Dispatchers.Main) {
            println("Main:        " + Thread.currentThread().name)
        }
        println("Fefault:     " + Thread.currentThread().name)
        println("Fefault:     " + Thread.currentThread().name)
        println("Fefault:     " + Thread.currentThread().name)
        GlobalScope.launch(Dispatchers.Default) {
            println("Default:     " + Thread.currentThread().name)
        }
        GlobalScope.launch(Dispatchers.Default) {
            println("Default:     " + Thread.currentThread().name)
        }
        GlobalScope.launch(Dispatchers.Unconfined) {
            println("Unconfined:  " + Thread.currentThread().name)
        }
        GlobalScope.launch(Dispatchers.Unconfined) {
            println("Unconfined:  " + Thread.currentThread().name)
        }
        GlobalScope.launch(Dispatchers.IO) {
            println("IO:          " + Thread.currentThread().name)
        }
        GlobalScope.launch(Dispatchers.IO) {
            println("IO:          " + Thread.currentThread().name)
        }
        delay(1000L)
    }
}