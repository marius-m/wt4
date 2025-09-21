import lt.markmerkk.Versions

plugins {
    id("kotlin")
}

dependencies {
    // implementation(project(":jira-client2"))
    api("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    api("org.apache.httpcomponents:httpclient:4.5.14")
    api("org.apache.httpcomponents:httpmime:4.2.5")
    implementation(files("${rootDir.absolutePath}/libs/${Versions.localJiraClient}"))

    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("joda-time:joda-time:${Versions.jodaTime}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    testImplementation(project(":mock-factory"))
}