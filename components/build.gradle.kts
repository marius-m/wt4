import lt.markmerkk.Versions

plugins {
    id("kotlin")
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":models"))
    implementation(project(":remote"))
    implementation(project(":database2"))

    // source: https://github.com/xdrop/fuzzywuzzy
    implementation("me.xdrop:fuzzywuzzy:1.2.0")
    implementation("joda-time:joda-time:${Versions.jodaTime}")
    implementation("io.reactivex:rxjava:${Versions.rxJava}")
    implementation("io.reactivex:rxjava-async-util:${Versions.rxJavaAsync}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("commons-io:commons-io:2.6")
    implementation("com.squareup.retrofit2:converter-gson:2.6.1")

    // Logging
    implementation("org.slf4j:jul-to-slf4j:${Versions.slf4j}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    testImplementation(project(":mock-factory"))
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
