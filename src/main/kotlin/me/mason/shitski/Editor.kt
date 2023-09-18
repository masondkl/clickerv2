package me.mason.shitski

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.awt.Robot
import java.awt.event.InputEvent
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.LockSupport
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.time.Duration.Companion.INFINITE
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun perlin(seed: Int, spread: Double): Double {
    return (sin(2.0 * seed / spread) + sin(PI * seed / spread))
}

//fun octaves(seeds: IntArray): Double {
//    val result =
//    seeds.indices.forEach {
//        perlin(seeds[it], )
//    }
//    return this
//}

fun DescriptiveStatistics.nextDelay(speed: Double): Long {
    val gaussian = ThreadLocalRandom.current().nextGaussian()
    val offset = ((gaussian * standardDeviation * 2) - standardDeviation)
    return (offset + mean / speed).roundToLong() * 1000000L
}

suspend fun editor(
    onPress: MutableList<suspend (Int) -> (Unit)>,
    onRelease: MutableList<suspend (Int) -> (Unit)>
) = coroutineScope<Unit> {
    val random = Random()
    val robot = Robot()
    val scanner = Scanner(System.`in`)
    val press = DescriptiveStatistics()
    val release = DescriptiveStatistics()
    var lastPress = -1L
    var lastRelease = -1L
    var recording = false
    var enabled = false
    var clicking = false
    var skipPress = false
    var skipRelease = false
    var speed = 1.0
    onPress += press@{
        if (it != 1) return@press
        if (recording) {
            val now = currentTimeMillis()
            if (lastPress != -1L) press.addValue((now - lastPress).toDouble())
            lastRelease = now
        } else if (enabled) {
            if (!skipPress) {
                clicking = true
                println("clicking")
            } else {
                skipPress = false
            }
        }
    }
    onRelease += release@{
        if (it != 1) return@release
        if (recording) {
            val now = currentTimeMillis()
            if (recording && lastRelease != -1L) release.addValue((now - lastRelease).toDouble())
            lastPress = now
        } else if (enabled) {
            if (!skipRelease) {
                clicking = false
            } else {
                skipRelease = false
            }
        }
    }
    launch {
        while (true) {
            if (enabled) break
            println("-------------------------------------------")
            println("Options")
            println("-------------------------------------------")
            println("1: Generate Distribution Statistics")
            println("2: Scale")
            println("3: Enter Clicker")
            println("-------------------------------------------")
            print("Choose: ")
            val option = scanner.nextInt()
            println()
            if (option == 1) {
                recording = true
                var i = 0
                while (i <= 10) {
                    print("Click for ${10 - i} more seconds\r")
                    i++
                    LockSupport.parkNanos(1.seconds.inWholeNanoseconds)
                }; print("\n")
                recording = false
                println()
                println("[PRESS] std dev: ${press.standardDeviation}, mean: ${press.mean}")
                println("[RELEASE] std dev: ${release.standardDeviation}, mean: ${release.mean}")
                println()
            } else if (option == 2) {
                print("Enter scale: ")
                speed = scanner.nextDouble()
                println()
                println("Speed: $speed")
                println()
            } else if (option == 3) {
                enabled = true
                println("enabled: $enabled")
            }
        }
    }
    launch {
        while(!enabled)
            LockSupport.parkNanos(10.milliseconds.inWholeNanoseconds)
//        println("enable")
        while (true) {
//            println(clicking)
            if (!clicking) {
                LockSupport.parkNanos(10.milliseconds.inWholeNanoseconds)
                continue
            }
//                if (!enabled || !clicking) { continue }
//                println("start performed click")
//            println("click")
            LockSupport.parkNanos(press.nextDelay(speed))
            if (clicking) {
                skipRelease = true
                robot.mouseRelease(InputEvent.getMaskForButton(1))
            }
            LockSupport.parkNanos(release.nextDelay(speed))
            if (clicking) {
                skipPress = true
                robot.mousePress(InputEvent.getMaskForButton(1))
            }
//                println("end performed click")
        }
    }
}