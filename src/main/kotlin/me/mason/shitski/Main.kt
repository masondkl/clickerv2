package me.mason.shitski

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent
import com.github.kwhat.jnativehook.mouse.NativeMouseListener
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.System.nanoTime

suspend fun main() = coroutineScope<Unit> {
    val onPress = ArrayList<suspend(Int) -> (Unit)>()
    val onRelease = ArrayList<suspend(Int) -> (Unit)>()
    GlobalScreen.registerNativeHook()
    val mouse = object : NativeMouseListener {
        override fun nativeMousePressed(nativeEvent: NativeMouseEvent) {
            runBlocking {
                onPress.forEach { it(nativeEvent.button) }
            }
        }
        override fun nativeMouseReleased(nativeEvent: NativeMouseEvent) {
            runBlocking {
                onRelease.forEach { it(nativeEvent.button) }
            }
        }
    }
    GlobalScreen.addNativeMouseListener(mouse)
    editor(onPress, onRelease)
}