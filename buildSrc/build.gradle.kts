import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
}

object PluginVersions {
    val junit = "4.12"
    val assertj = "3.12.0"
    val mockito = "2.23.4"
    val mockito_kotlin = "2.0.0"
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")

    implementation(gradleApi())
    implementation(localGroovy())

    testImplementation("junit:junit:${PluginVersions.junit}")
    testImplementation("org.mockito:mockito-core:${PluginVersions.mockito}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${PluginVersions.mockito_kotlin}")
    testImplementation("org.assertj:assertj-core:${PluginVersions.assertj}")
}

gradlePlugin {
    plugins {
        create("jbundle") {
            id = "lt.markmerkk.jbundle"
            implementationClass = "lt.markmerkk.export.JBundlePlugin"
        }
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}
