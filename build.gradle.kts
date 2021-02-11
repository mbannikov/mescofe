import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

val kotlinVersion by extra { "1.4.20" }

plugins {
    kotlin("jvm") version "1.4.20"
    id("maven-publish")
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
    apply(plugin = "maven-publish")

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

    publishing {
        publications {
            create<MavenPublication>("myLibrary") {
                from(components["java"])
            }
        }

        repositories {
            maven {
                name = "mescofe"
                url = URI("https://maven.pkg.github.com/mbannikov/mescofe")
                credentials {
                    username = System.getenv("MAVEN_REGISTRY_USERNAME")
                    password = System.getenv("MAVEN_REGISTRY_PASSWORD")
                }
            }
        }
    }
}
