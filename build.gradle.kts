plugins {
    kotlin("jvm").version("1.9.0").apply(true)
    id("com.github.johnrengelman.shadow").version("6.0.0")
}

group = "me.mason"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.kwhat:jnativehook:2.2.2")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.shadowJar {
    archiveFileName.set("${project.name}.jar")
    destinationDirectory.set(file("server/plugins"))
}

tasks.compileKotlin.get().kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs = listOf(
        "-Xcontext-receivers",
        "-Xinline-classes",
        "-Xopt-in=kotlin.time.ExperimentalTime",
        "-Xopt-in=kotlin.contracts.ExperimentalContracts",
        "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xopt-in=kotlinx.coroutines.DelicateCoroutinesApi"
    )
}