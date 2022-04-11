import lt.markmerkk.Versions

plugins {
    id("kotlin")
}

sourceSets {
    main {
        java {
            srcDirs("generate/src")
        }
    }
}

dependencies {
    implementation(project(":models"))
    implementation(
            fileTree("libs")
                    .include("*.jar")
    )
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("joda-time:joda-time:${Versions.jodaTime}")
    implementation("io.reactivex:rxjava:${Versions.rxJava}")
    implementation("javax.annotation:javax.annotation-api:${Versions.javaAnnotate}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.kotlinTest}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    testImplementation(project(":mock-factory"))
}
