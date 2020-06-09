import lt.markmerkk.Versions

plugins {
    id("kotlin")
}

dependencies {
    implementation(project(":models"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("joda-time:joda-time:${Versions.jodaTime}")

    implementation("org.mockito:mockito-core:${Versions.mockito}")
    implementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
}