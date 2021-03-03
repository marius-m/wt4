import lt.markmerkk.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import lt.markmerkk.exportextra.JBundleExtraPropsFactory

plugins {
    id("application")
    id("kotlin")
    id("kotlin-kapt")
    id("idea")
    id("com.github.johnrengelman.shadow")
    id("de.fuerstenau.buildconfig")
    id("lt.markmerkk.jbundle")
}

// val jBundleProps = JBundleExtraPropsFactory.Debug.asBasic("debug", project)
//val jBundleProps = JBundleExtraPropsFactory.Debug.asOauthITO("debug", project)
//val jBundleProps = JBundleExtraPropsFactory.Release.asBasicWin("basic", project)
// val jBundleProps = JBundleExtraPropsFactory.Release.asBasicMac("basic", project)
// val jBundleProps = JBundleExtraPropsFactory.Release.asBasicLinux("basic", project)
//val jBundleProps = JBundleExtraPropsFactory.Release.asOauthITOWin("iTo", project)
// val jBundleProps = JBundleExtraPropsFactory.Release.asOauthITOMac("iTo", project)
val jBundleProps = JBundleExtraPropsFactory.Release.asOauthITOLinux("iTo", project)
//val jBundleProps = JBundleExtraPropsFactory.Release.asOauthITOCustomSystemWideWindows("iToSW", project)

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

    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")
    implementation("com.brsanthu:google-analytics-java:1.1.2")
    implementation("com.google.guava:guava:21.0")
    implementation("com.jfoenix:jfoenix:9.0.10")
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
    implementation("org.controlsfx:controlsfx:11.0.2")
    implementation("javax.annotation:javax.annotation-api:${Versions.javaAnnotate}")

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
    setVersion(jBundleProps.versionName)
    applicationDefaultJvmArgs = listOf(
            "-Xms128M",
            "-Xmx300M",
            "-XX:+UseG1GC"
//            "-DWT_ROOT=/Users/mariusmerkevicius/tmp-wt4",
//            "-DWT_APP_PATH=wt4_debug"
    )
}

buildConfig {
    appName = jBundleProps.appName
    version = jBundleProps.versionName
    packageName = "lt.markmerkk"

    buildConfigField("String", "flavor", jBundleProps.appFlavor)
    buildConfigField("String", "versionName", jBundleProps.versionName)
    buildConfigField("int", "versionCode", jBundleProps.versionCode.toString())
    buildConfigField("boolean", "debug", jBundleProps.debug.toString())
    buildConfigField("String", "gaKey", jBundleProps.gaKey)
    buildConfigField("boolean", "oauth", jBundleProps.oauth.toString())
    buildConfigField("String", "oauthKeyConsumer", jBundleProps.oauthKeyConsumer)
    buildConfigField("String", "oauthKeyPrivate", jBundleProps.oauthKeyPrivate)
    buildConfigField("String", "oauthHost", jBundleProps.oauthHost)

    charset = Charsets.UTF_8.toString()
}

extensions.getByType(lt.markmerkk.export.tasks.JBundleExtension::class.java).apply {
    appName = jBundleProps.appName
    version = jBundleProps.versionName
    mainClassName = "lt.markmerkk.MainAsJava"
    mainJarFilePath = File(buildDir, "${File.separator}libs${File.separator}app-${jBundleProps.versionName}.jar").absolutePath
    systemWide = jBundleProps.systemWide
    jvmProps = jBundleProps.jvmProps

    mainIconFilePath = File(projectDir, "package${File.separator}icons${File.separator}App1024.png").absolutePath
    scriptsDirPath = File(projectDir, "package${File.separator}scripts").absolutePath
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
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
