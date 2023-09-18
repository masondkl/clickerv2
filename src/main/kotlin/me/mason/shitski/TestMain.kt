package me.mason.shitski

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() = coroutineScope<Unit> {
    launch {
        while(true) {
            delay(50)
            println("a")
        }
    }
    launch {
        while(true) {
            delay(50)
            println("b")
        }
    }
}