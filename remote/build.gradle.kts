import lt.markmerkk.Versions

plugins {
    id("kotlin")
}

dependencies {
//    implementation(project(":jira-client2"))

    // Using local file instead of a library, as the gradle config would require changes to use it as a code
    // Leaving it as intact as possible
    // To build the library locally you'll need to apply a patch that would disable some parts of the gradle config
    // Patch is located: ${projectModule}/jira-client-build.patch
    // So: 'git apply jira-client-build.patch'
    api("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    api("org.apache.httpcomponents:httpclient:4.5.14")
    api("org.apache.httpcomponents:httpmime:4.2.5")
    implementation(files("${rootDir.absolutePath}/libs/${Versions.localJiraClient}"))

    implementation(project(":database2"))
    implementation(project(":models"))

    implementation("joda-time:joda-time:${Versions.jodaTime}")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    implementation("io.reactivex:rxjava:${Versions.rxJava}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("io.reactivex:rxjava-async-util:${Versions.rxJavaAsync}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    testImplementation(project(":mock-factory"))
}