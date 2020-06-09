import lt.markmerkk.Versions

plugins {
    id("kotlin")
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.commons:commons-io:1.3.2")
    implementation("io.reactivex:rxjava:${Versions.rxJava}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${Versions.log4j}")
}

