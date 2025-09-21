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
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.25")

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