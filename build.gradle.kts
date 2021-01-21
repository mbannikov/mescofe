import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

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
    group = "ru.mbannikov"
    version = "1.0.0"

    apply<KotlinPlatformJvmPlugin>()

    dependencies {
        api(kotlin("stdlib-jdk8", kotlinVersion))

        testImplementation(kotlin("test-junit5"))
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.5.2")
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
