import lt.markmerkk.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("kotlin")
    id("kotlin-kapt")
    id("idea")
    id("com.github.johnrengelman.shadow")
    id("de.fuerstenau.buildconfig")
    id("lt.markmerkk.jbundle")
}

val versionName: String by project
val versionCode: String by project
val debug: Boolean by project
val gaKey: String by project
val oauth: Boolean by project
val oauthKeyConsumer: String by project
val oauthKeyPrivate: String by project
val oauthHost: String by project

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/kapt/main")
        }
    }
    test {
        java {
            srcDirs("build/generated/source/kapt/main")
        }
    }
}


dependencies {
//    implementation(fileTree("libs", include: ["*.jar"]))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    implementation(project(":components"))
    implementation(project(":database2"))
    implementation(project(":remote"))
    implementation(project(":jira-client"))
    implementation(project(":models"))
    implementation(project(":mock-factory"))
    implementation(project(":credits"))

    implementation("no.tornado:tornadofx:1.7.19")
    implementation("com.brsanthu:google-analytics-java:1.1.2")
    implementation("com.google.guava:guava:21.0")
    implementation("com.jfoenix:jfoenix:8.0.8")
    implementation("io.reactivex:rxjavafx:1.1.0")
    implementation("io.reactivex:rxjava:${lt.markmerkk.Versions.rxJava}")
    implementation("io.reactivex:rxjava-async-util:${lt.markmerkk.Versions.rxJavaAsync}")
    implementation("org.bouncycastle:bcprov-jdk15on:1.51")
    implementation("com.google.protobuf:protobuf-java:3.7.0")
    implementation("org.scribe:scribe:1.3.7")
    implementation("com.squareup.okhttp3:okhttp:3.14.2")
    implementation("com.squareup.okhttp3:logging-interceptor:3.14.2")
    implementation("com.squareup.retrofit2:adapter-rxjava:2.6.1")
    implementation("com.squareup.retrofit2:converter-gson:2.6.1")
    implementation("commons-io:commons-io:2.6")
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("org.controlsfx:controlsfx:8.40.16")

    implementation("com.google.dagger:dagger:${Versions.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    implementation("joda-time:joda-time:${Versions.jodaTime}")
    implementation("com.calendarfx:view:8.5.0")

    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${Versions.log4j}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
}

project.extensions.getByType(JavaApplication::class.java).apply {
    mainClassName = "lt.markmerkk.MainAsJava"
    group = "lt.markmerkk"
    setVersion(versionName)
    val logConfig = if (debug) "debug_log4j2.xml" else "prod_log4j2.xml"
    applicationDefaultJvmArgs = listOf(
            "-Dlog4j.configurationFile=$logConfig",
            "-Xms128M",
            "-Xmx300M",
            "-XX:+UseG1GC"
    )
}

buildConfig {
    appName = "WT4"
    version = versionName
    packageName = "lt.markmerkk"

    buildConfigField("String", "versionName", versionName)
    buildConfigField("int", "versionCode", versionCode)
    buildConfigField("boolean", "debug", debug.toString())
    buildConfigField("String", "gaKey", gaKey)
    buildConfigField("boolean", "oauth", oauth.toString())
    buildConfigField("String", "oauthKeyConsumer", oauthKeyConsumer)
    buildConfigField("String", "oauthKeyPrivate", oauthKeyPrivate)
    buildConfigField("String", "oauthHost", oauthHost)

    charset = Charsets.UTF_8.toString()
}

extensions.getByType(lt.markmerkk.export.tasks.JBundleExtension::class.java).apply {
    appName = "app"
    version = versionName
    mainClassName = "lt.markmerkk.MainAsJava"
    mainJarFilePath = File(buildDir, "/libs/app-${versionName}.jar").absolutePath

    mainIconFilePath = File(projectDir, "icons/App1024.png").absolutePath
    scriptsDirPath = File(projectDir, "scripts").absolutePath
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kapt {
    generateStubs = true
}

idea {
    module {
        sourceDirs = sourceDirs
                .plus(file("generated/"))
    }
}
