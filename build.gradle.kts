buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://maven.atlassian.com/content/repositories/atlassian-public")
        maven("http://gradle.artifactoryonline.com/gradle/libs/")
        maven("http://maven.wso2.org/nexus/content/groups/wso2-public/")
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${lt.markmerkk.Versions.kotlin}")
        classpath("com.github.jengelman.gradle.plugins:shadow:4.0.2")
        classpath("de.dynamicfiles.projects.gradle.plugins:javafx-gradle-plugin:8.8.2")
        classpath("gradle.plugin.de.fuerstenau:BuildConfigPlugin:1.1.8")
    }
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}
