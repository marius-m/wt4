import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

object Versions {
    val junit = "4.12"
    val assertj = "3.12.0"
    val mockito = "2.23.4"
    val mockito_kotlin = "2.0.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")

    implementation(gradleApi())
    implementation(localGroovy())

    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito_kotlin}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
}