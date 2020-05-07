buildscript {
//    apply from: "versions.gradle"
//    ext.kotlin_test_version = '1.4.0'


    // Version
//    ext.versionName = "1.7.8"
//    ext.versionCode = "63"

    //region Debug oauth properties
    // Generate keys: https://confluence.atlassian.com/jirakb/how-to-generate-public-key-to-application-link-3rd-party-applications-913214098.html
//    def keysPropertyFile = new File("${rootDir}/keys_debug", "private.properties")
//    assert keysPropertyFile.exists()
//    Properties keysProperties = new Properties()
//    keysProperties.load(new FileInputStream(keysPropertyFile.getAbsolutePath()))
//
//    ext.debug = true
//    ext.gaKey = "test"
//    ext.oauth = true
//    ext.oauth_key_consumer = keysProperties.getProperty("key_consumer")
//    ext.oauth_key_private = keysProperties.getProperty("key_private")
//    ext.oauth_host = keysProperties.getProperty("host")
    //endregion

    //region Debug basic properties
    //endregion

    //region Release oauth properties
//    def keysPropertyFile = new File("${rootDir}/keys", "private.properties")
//    assert keysPropertyFile.exists()
//    Properties keysProperties = new Properties()
//    keysProperties.load(new FileInputStream(keysPropertyFile.getAbsolutePath()))
//
//    def deployPropertyFile = new File("${rootDir}","deploy.properties")
//    assert deployPropertyFile.exists()
//    Properties deployProps = new Properties()
//    deployProps.load(new FileInputStream(deployPropertyFile.getAbsolutePath()))
//    ext.debug = false
//    ext.gaKey = deployProps.getProperty("ga")
//    ext.oauth = true
//    ext.oauth_key_consumer = keysProperties.getProperty("key_consumer")
//    ext.oauth_key_private = keysProperties.getProperty("key_private")
//    ext.oauth_host = keysProperties.getProperty("host")
    //endregion

    //region Release basic properties
//    def deployPropertyFile = new File("${rootDir}","deploy.properties")
//    assert deployPropertyFile.exists()
//    Properties deployProps = new Properties()
//    deployProps.load(new FileInputStream(deployPropertyFile.getAbsolutePath()))
//
//    ext.debug = false
//    ext.gaKey = deployProps.getProperty("ga")
//    ext.oauth = false
//    ext.oauth_key_consumer = ""
//    ext.oauth_key_private = ""
//    ext.oauth_host = ""
    //endregion

    repositories {
        mavenLocal()
        jcenter()
        maven("https://maven.atlassian.com/content/repositories/atlassian-public")
        maven("https://repo.maven.apache.org/maven2")
        maven("http://gradle.artifactoryonline.com/gradle/libs/")
        maven("http://maven.wso2.org/nexus/content/groups/wso2-public/")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${lt.markmerkk.Versions.kotlin}")
        classpath("com.github.jengelman.gradle.plugins:shadow:4.0.2")
        classpath("de.dynamicfiles.projects.gradle.plugins:javafx-gradle-plugin:8.8.2")
        classpath("gradle.plugin.de.fuerstenau:BuildConfigPlugin:1.1.8")
    }
}

allprojects {
    ext {
        set("versionName", "1.7.8")
        set("versionCode", "63")
        set("debug", true)
        set("gaKey", "test")
        set("oauth", false)
        set("oauthKeyConsumer", "")
        set("oauthKeyPrivate", "")
        set("oauthHost", "")
    }

    repositories {
        mavenCentral()
        jcenter()
    }
}
