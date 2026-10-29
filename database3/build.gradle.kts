import lt.markmerkk.Versions

plugins {
    id("kotlin")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("WT4Database") {
            packageName.set("lt.markmerkk.db")
            dialect("app.cash.sqldelight:sqlite-3-25-dialect:${Versions.sqldelight}")
        }
    }
}

dependencies {
    implementation(project(":models"))
    implementation("app.cash.sqldelight:sqlite-driver:${Versions.sqldelight}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("joda-time:joda-time:${Versions.jodaTime}")
    implementation("io.reactivex:rxjava:${Versions.rxJava}")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    testImplementation(project(":mock-factory"))
}
