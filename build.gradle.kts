import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion by extra { "1.4.20" }

plugins {
    kotlin("jvm") version "1.4.20"
}

allprojects {
    repositories {
        jcenter()
    }
}

subprojects {
    group = "ru.mbannikov.mescofe"
    version = "1.0.0"

    apply<KotlinPlatformJvmPlugin>()
    apply(plugin = "org.gradle.maven")

    dependencies {
        api(kotlin("stdlib-jdk8", kotlinVersion))
        api("io.github.microutils:kotlin-logging:1.12.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}
