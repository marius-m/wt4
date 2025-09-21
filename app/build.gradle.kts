import lt.markmerkk.Versions
import lt.markmerkk.exportextra.AppType
import lt.markmerkk.exportextra.JBundleExtraPropsFactory

plugins {
    id("application")
    id("kotlin")
    id("kotlin-kapt")
    id("idea")
    id("com.gradleup.shadow")
    id("de.fuerstenau.buildconfig")
    id("lt.markmerkk.jbundle")
    id("org.openjfx.javafxplugin")
}

val jBundleProps = JBundleExtraPropsFactory.Debug.asBasic(AppType.DEBUG, project)
// val jBundleProps = JBundleExtraPropsFactory.Release.asBasicWin(AppType.BASIC, project)
// val jBundleProps = JBundleExtraPropsFactory.Release.asBasicMac(AppType.BASIC, project)
// val jBundleProps = JBundleExtraPropsFactory.Release.asBasicLinux(AppType.BASIC, project)

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/kapt/main", "build/gen/buildconfig/src/main")
        }
    }
    test {
        java {
            srcDirs("build/generated/source/kapt/main")
        }
    }
}

dependencies {
    // implementation(fileTree("libs", include: ["*.jar"]))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    implementation(project(":components"))
    implementation(project(":database2"))
    implementation(project(":remote"))

//    api(project(":jira-client2"))
    api(files("${rootDir.absolutePath}/libs/${Versions.localJiraClient}"))
    implementation(project(":models"))
    implementation(project(":mock-factory"))
    implementation(project(":credits"))

    implementation("com.googlecode.blaisemath.tornado:tornadofx-fx18k16:2.0.1")
    implementation("com.brsanthu:google-analytics-java:1.1.2")
    implementation("com.google.guava:guava:21.0")
    implementation(files("${rootDir.absolutePath}/libs/${Versions.localJFoenix21}"))
    implementation("io.reactivex:rxjavafx:1.1.0")
    implementation("io.reactivex:rxjava:${Versions.rxJava}")
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
    implementation("org.fxmisc.richtext:richtextfx:0.11.6")
    implementation("org.jsoup:jsoup:1.15.2")

    implementation("com.google.dagger:dagger:${Versions.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    implementation("joda-time:joda-time:${Versions.jodaTime}")
    implementation("com.calendarfx:view:10.5.0")

    implementation("io.sentry:sentry:${Versions.sentry}")
    implementation("io.sentry:sentry-logback:${Versions.sentry}")
    implementation("org.slf4j:jul-to-slf4j:${Versions.slf4j}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
}

project.extensions.getByType(JavaApplication::class.java).apply {
    mainClass = "lt.markmerkk.MainAsJava"
    group = "lt.markmerkk"
    setVersion(jBundleProps.versionName)
    applicationDefaultJvmArgs = listOf(
//            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
            "-Xms128M",
            "-Xmx300M",
            "-XX:+UseG1GC",
            "--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
            "--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED",
            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
            // "-DWT_ROOT=/Users/mariusmerkevicius/tmp-wt4",
           // "-DWT_APP_PATH=${jBundleProps.app}"
    ).plus(jBundleProps.jvmProps)
}

// Required for BuildConfig plugin to workaround the not up to date plugin issue with newer gradle
// https://github.com/mfuerstenau/gradle-buildconfig-plugin/issues/30
configurations {
    create("compile")
}

tasks {
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

buildConfig {
    appName = jBundleProps.appName
    version = jBundleProps.versionName
    packageName = "lt.markmerkk"

    buildConfigField("String", "flavor", jBundleProps.appFlavor)
    buildConfigField("String", "versionName", jBundleProps.versionName)
    buildConfigField("int", "versionCode", jBundleProps.versionCode.toString())
    buildConfigField("boolean", "debug", jBundleProps.debug.toString())
    buildConfigField("String", "sentryDsn", jBundleProps.sentryDsn)
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
    mainJarName = "app-${jBundleProps.versionName}"
    mainClassName = "lt.markmerkk.MainAsJava"
    mainJarFilePath = File(buildDir, "${File.separator}libs${File.separator}${mainJarName}.jar").absolutePath
    systemWide = jBundleProps.systemWide
    jvmProps = jBundleProps.jvmProps

    mainIconFilePath = File(projectDir, "package${File.separator}icons${File.separator}App1024.png").absolutePath
    scriptsDirPath = File(projectDir, "package${File.separator}scripts").absolutePath
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

javafx {
    version = "21.0.2"
    modules(
        "javafx.base",
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.media",
        "javafx.swing",
        "javafx.web"
    )
}
