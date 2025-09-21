buildscript {
    repositories {
        mavenCentral()
        maven("https://maven.atlassian.com/content/repositories/atlassian-public")
        maven("https://maven.wso2.org/nexus/content/groups/wso2-public/")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${lt.markmerkk.Versions.kotlin}")
        classpath("com.gradleup.shadow:shadow-gradle-plugin:9.0.0")
        classpath("de.dynamicfiles.projects.gradle.plugins:javafx-gradle-plugin:8.8.2")
        classpath("gradle.plugin.de.fuerstenau:BuildConfigPlugin:1.1.8")
        classpath("org.openjfx:javafx-plugin:0.1.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
        // maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
