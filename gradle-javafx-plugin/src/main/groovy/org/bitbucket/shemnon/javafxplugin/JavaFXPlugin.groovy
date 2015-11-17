/*
 * Copyright (c) 2012, 2014 Danno Ferrin
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions are met:
 *       * Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *       * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *       * Neither the name of Danno Ferrin nor the
 *         names of contributors may be used to endorse or promote products
 *         derived from this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bitbucket.shemnon.javafxplugin

import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.bitbucket.shemnon.javafxplugin.tasks.JavaFXDeployTask
import org.bitbucket.shemnon.javafxplugin.tasks.JavaFXJarTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.bitbucket.shemnon.javafxplugin.tasks.JavaFXCSSToBinTask
import org.bitbucket.shemnon.javafxplugin.tasks.JavaFXSignJarTask
import org.bitbucket.shemnon.javafxplugin.tasks.GenKeyTask
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.internal.os.OperatingSystem


class JavaFXPlugin implements Plugin<Project> {

    public static final String PROVIDED_COMPILE_CONFIGURATION_NAME = "providedCompile";
    public static final String PROVIDED_RUNTIME_CONFIGURATION_NAME = "providedRuntime";

    static String getOSProfileName() {
        def currentOS = OperatingSystem.current();
        if (currentOS.isWindows()) {
            return 'windows'
        }
        if (currentOS.isLinux()) {
            return 'linux'
        }
        if (currentOS.isMacOsX()) {
            return 'macosx'
        }

        return null;
    }

    private Project project
    
    @Lazy private String[] profiles = ([] + project.getProperties().profiles?.split(',') + getOSProfileName()).flatten().findAll {
            project.javafx.getProfile(it) != null
        }

    protected basicExtensionMapping = {prop, convention = null, aware = null ->
        JavaFXPluginExtension ext = project.javafx;
        for (profile in profiles) {
            Map override = ext.getProfile(profile)
            def val = override[prop]
            if (val != null) {
                if (val instanceof Map) {
                    def ov = ext[prop]
                    if (ov instanceof Map) {
                        return [:] + ext[prop] + val
                    }
                }
                return val;
            }
        }
        return ext[prop]
    }
    
    protected mainClassConvention = {convention, aware ->
        if (!project.javafx.mainClass) { 
            def mains = []
            sourceSet(project).allJava.visit {
                if (it.relativePath.lastName == 'Main.java') {
                    mains.add(it.relativePath.replaceLastName('Main').pathString.replace('/', '.'))
                }
            }
    
            if (mains.size() == 1) {
                project.javafx.mainClass = mains[0]
            }
        }
        return project.javafx.mainClass
    }


    @Override
    void apply(Project project) {
        this.project = project

        project.getPlugins().apply(JavaPlugin)
        project.extensions.create('javafx', JavaFXPluginExtension)

        configureConfigurations(project.configurations)

        def jfxrtJarFile = project.javafx.jfxrtJar ?: project.files(findJFXJar())
        project.javafx {
            jfxrtJar = jfxrtJarFile
            antJavaFXJar = antJavaFXJar ?: project.files(findAntJavaFXJar())
            appName = project.name //FIXME capatalize
            packaging = 'all'
            signingMode = 'release'
        }


        project.dependencies {
            providedCompile jfxrtJarFile
        }
        project.sourceSets {
            'package' {
                resources {
                    srcDir 'src/deploy/package'
                    srcDir 'src/deploy/resources'
                }
            }
        }

        configureJavaFXCSSToBinTask(project)
        configureJavaFXJarTask(project)
        configureGenerateDebugKeyTask(project)
        configureJavaFXSignJarTask(project)
        configureJFXCopyLibsTask(project)
        configureJFXDeployTask(project)
        configureScenicViewTask(project)
        configureRunTask(project)
        configureDebugTask(project)

    }


    private configureJavaFXCSSToBinTask(Project project) {
        def task = project.tasks.replace("cssToBin", JavaFXCSSToBinTask)
        task.description =  "Converts CSS to Binary CSS."
        task.group =  'Build'
        task.enabled = false // Use of BSS is uncommon, and can slow down/bloat builds

        task.conventionMapping.distsDir = {convention, aware -> sourceSet(project).output.resourcesDir}

        task.conventionMapping.inputFiles = {convention, aware ->
            sourceSet(project).resources
        }

        project.tasks.getByName("classes").dependsOn(task)
        task.dependsOn(project.tasks.getByName("processResources"))
    }

    private configureJavaFXJarTask(Project project) {
        def task = project.tasks.replace("jfxJar", JavaFXJarTask)
        task.description = "Adds JavaFX specific packaging to the jar."
        task.group = 'Build'
        
        project.afterEvaluate {
            project.configurations.archives.artifacts*.builtBy task
            task.dependsOn = [sourceSet(project).jarTaskName]
        }

        [
                'embedLauncher',
                'arguments'
        ].each {prop -> task.conventionMapping[prop] = basicExtensionMapping.curry(prop) }

        task.conventionMapping.mainClass = mainClassConvention

        task.conventionMapping.jarFile = {convention, aware ->
            project.tasks.getByName(sourceSet(project).jarTaskName).archivePath
        }
        task.conventionMapping.classpath = {convention, aware ->
            FileCollection compileClasspath = sourceSet(project).compileClasspath;
            Configuration providedCompile = project.configurations[PROVIDED_COMPILE_CONFIGURATION_NAME];
            return compileClasspath - providedCompile;
        }
    }

    private configureGenerateDebugKeyTask(Project project) {
        def task = project.tasks.replace("generateDebugKey", GenKeyTask)
        task.description = "Generates the JavaFX Debug Key."
        task.group = 'Build'

        project.afterEvaluate {
            task.enabled = task.enabled && project.javafx.debugKey != null
        }

        task.conventionMapping.alias     = {convention, aware -> project.javafx.debugKey?.alias }
        task.conventionMapping.keyPass   = {convention, aware -> project.javafx.debugKey?.keyPass }
        task.conventionMapping.keyStore  = {convention, aware -> project.javafx.debugKey?.keyStore }
        task.conventionMapping.storePass = {convention, aware -> project.javafx.debugKey?.storePass }
        task.conventionMapping.storeType = {convention, aware -> project.javafx.debugKey?.storeType }
        task.conventionMapping.dname     = {convention, aware -> 'CN=JavaFX Gradle Plugin Default Debug Key, O=JavaFX Debug' }
        task.conventionMapping.validity  = {convention, aware -> ((365.25) * 25 as int) /* 25 years */ }
    }

    private configureJavaFXSignJarTask(Project project) {
        def task = project.tasks.replace("jfxSignJar", JavaFXSignJarTask)
        task.description = "Signs the JavaFX jars the JavaFX way."
        task.group = 'Build'
        task.dependsOn = ['jfxJar']
        
        project.afterEvaluate {
            project.configurations.archives.artifacts*.builtBy task
            task.enabled = task.enabled && (project.javafx.debugKey != null || project.javafx.releaseKey != null)
        }

        ['alias', 'keyPass', 'storePass', 'storeType'].each { prop ->
            task.conventionMapping[prop]  = {convention, aware ->
                def jfxc = project.javafx;
                def props = project.properties
                def mode = props['javafx.signingMode']  ?: jfxc.signingMode
                return props?."javafx.${mode}Key.$prop" ?: jfxc?."${mode}Key"?."$prop"
            }
        }
        task.conventionMapping.keyStore  = {convention, aware ->
            def jfxc = project.javafx;
            def props = project.properties
            def mode = props['javafx.signingMode']  ?: jfxc.signingMode
            String keyFile = props?."javafx.${mode}Key.keyStore"
            return keyFile == null ? jfxc?."${mode}Key"?.keyStore : new File(project.projectDir, keyFile)
        }

        task.conventionMapping.outdir = {convention, aware -> project.libsDir}

        task.conventionMapping.inputFiles = {convention, aware ->
            FileCollection runtimeClasspath = sourceSet(project).runtimeClasspath;
            Configuration providedRuntime = project.configurations[PROVIDED_RUNTIME_CONFIGURATION_NAME];
            project.files(runtimeClasspath - providedRuntime, project.configurations.archives.artifacts.files.collect{it})
        }

        task.dependsOn(project.tasks.getByName("jfxJar"))
    }

    private configureJFXCopyLibsTask(Project project) {
        def task = project.tasks.replace("jfxCopyLibs")

        task.doLast {
            FileCollection runtimeClasspath = sourceSet(project).runtimeClasspath;
            Configuration providedRuntime = project.configurations[PROVIDED_RUNTIME_CONFIGURATION_NAME];
            project.files(runtimeClasspath - providedRuntime, project.configurations.archives.artifacts.files.collect{it}).
                    findAll {File f -> f.exists() && !f.directory}.
                    each {
                        ant.copy(
                                file: it,
                                toDir: project.libsDir,
                                overwrite: false
                        )
                    }
        }

        task.dependsOn(project.tasks.getByName("jfxSignJar"))
        task.dependsOn(project.tasks.getByName("jfxJar"))
    }

    private configureJFXDeployTask(Project project) {
        def task = project.tasks.replace("jfxDeploy", JavaFXDeployTask)
        task.description = "Processes the JavaFX jars and generates webstart and native packages."
        task.group = 'Build'

        [
                'antJavaFXJar',
                'appID',
                'appName',
                'arguments',
                'bundleArguments',
                'category',
                'codebase',
                'copyright',
                'description',
                'embedJNLP',
                'height',
                'iconInfos',
                'id',
                'installSystemWide',
                'javaRuntime',
                'jvmArgs',
                'licenseType',
                'menu',
                'offlineAllowed',
                'packaging',
                'shortcut',
                'systemProperties',
                'updateMode',
                'vendor',
                'verbose',
                'width',
        ].each {prop -> task.conventionMapping[prop] = basicExtensionMapping.curry(prop) }

        task.conventionMapping.version = {convention, aware -> ('unspecified' == project.version) ? '0.0.0' : project.version }
        task.conventionMapping.mainClass = mainClassConvention

        task.conventionMapping.inputFiles = {convention, aware ->
            project.fileTree(project.libsDir).include("*.jar")
        }
        task.conventionMapping.resourcesDir = { convention, aware ->
            def rd = project.sourceSets['package'].output.resourcesDir
            if (!rd.exists()) rd.mkdirs()
            rd
        }

        task.conventionMapping.distsDir = {convention, aware -> project.distsDir }

        task.dependsOn(project.tasks.getByName("jfxCopyLibs"))
        task.dependsOn(project.tasks.getByName("packageClasses"))

        project.tasks.getByName("assemble").dependsOn(task)
    }
    
    private void configureRunTask(Project project) {
        JavaExec task = project.tasks.replace("run", JavaExec)
        task.description = 'Runs the application.'
        task.group = 'Execution'

        configureRunParams(task)
    }

    protected void configureRunParams(JavaExec task) {
        task.conventionMapping.main = mainClassConvention
        project.afterEvaluate({project ->
            task.classpath = sourceSet(project).runtimeClasspath
        })

        task.doFirst {
            task.jvmArgs basicExtensionMapping('jvmArgs')
            task.systemProperties basicExtensionMapping('systemProperties')
            if (!task.args) task.args = basicExtensionMapping('arguments')
        }
    }

    private void configureDebugTask(Project project) {
        JavaExec task = project.tasks.replace("debug", JavaExec)
        task.description = 'Runs the applicaiton and sets up debugging on port 5005.'
        task.group = 'Execution'

        configureRunParams(task)
        task.debug = true
    }

    private void configureScenicViewTask(Project project) {
        def task = project.tasks.replace("scenicview", DefaultTask)
        task.description = 'Adds the ScenicView agent to all Execution Tasks.'
        task.group = 'Tools'

        task.doLast {
            project.configurations {
                scenicview
            }

            if (JavaVersion.current().java8Compatible) {
                project.repositories {
                    maven  { url 'http://dl.bintray.com/scenic-view/scenic-view' }
                }
                project.dependencies {
                    scenicview('org.scenic-view:scenic-view:8.0.0-dp4') {
                        exclude group: 'org.fxconnector'
                    }
                }
            } else {
                project.repositories {
                    ivy  { url 'https://repository-javafx-gradle-plugin.forge.cloudbees.com/release' }
                }
                project.dependencies {
                    scenicview 'com.fxexperience.scenicview:scenicview:1.3.0'
                }
            }

            project.tasks.findAll {it.group == 'Execution' && it instanceof JavaExec}.each {JavaExec execTask ->
                project.configurations.getByName('scenicview').resolvedConfiguration.resolvedArtifacts.each { ResolvedArtifact ra ->
                    execTask.jvmArgs = ["-javaagent:$ra.file.canonicalPath"] + execTask.jvmArgs
                }
            }
        }
    }

    public void configureConfigurations(ConfigurationContainer configurationContainer) {
        Configuration provideCompileConfiguration = configurationContainer.create(PROVIDED_COMPILE_CONFIGURATION_NAME).setVisible(false).
                setDescription("Additional compile classpath for libraries that should not be part of the bundle.");
        Configuration provideRuntimeConfiguration = configurationContainer.create(PROVIDED_RUNTIME_CONFIGURATION_NAME).setVisible(false).
                extendsFrom(provideCompileConfiguration).
                setDescription("Additional runtime classpath for libraries that should not be part of the bundle.");
        configurationContainer.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(provideCompileConfiguration);
        configurationContainer.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME).extendsFrom(provideRuntimeConfiguration);
    }

    public sourceSet(Project project) {
        return project.javafx.getSourceSet('sourceSet', project)
    }



    private File searchFile(Map<String, Closure> places, List<String> searchPaths, String searchID) {
        File result = null;
        places.each { k, v ->
            if (result != null) return;
            project.logger.debug("Looking for $searchID in $k")
            def dir = v()
            if (dir == null) {
                project.logger.debug("$k not set")
            } else {
                project.logger.debug("$k is $dir")
                searchPaths.each { s ->
                    if (result != null) return;
                    File f = new File(dir, s);
                    project.logger.debug("Trying $f.path")
                    if (f.exists() && f.file) {
                        project.logger.debug("found $searchID as $result")
                        result = f; 
                    }
                }
            }
        }
        if (!result?.file) {
            throw new GradleException("Could not find $searchID, please set one of ${places.keySet()}");
        } else {
            project.logger.info("$searchID: ${result}")
            return result
        }
    }

    public File findJFXJar() {
        try {
            assert (project.jfxrtDir != null)
        } catch (RuntimeException ignored) {
            project.ext.jfxrtDir = "."
        }
        
        return searchFile([
                    'jfxrtDir in Gradle Properties': {project.jfxrtDir},
                    'JFXRT_HOME in System Environment': {System.env['JFXRT_HOME']},
                    'JAVA_HOME in System Environment': {System.env['JAVA_HOME']},
                    'java.home in JVM properties': {System.properties['java.home']}
                ],
                ['jfxrt.jar', 'lib/jfxrt.jar', 'lib/ext/jfxrt.jar', 'jre/lib/jfxrt.jar', 'jre/lib/ext/jfxrt.jar'],
                'JavaFX Runtime Jar')
    }

    public File findAntJavaFXJar() {
        return searchFile([
                    'jfxrtDir in Gradle Properties': {project.jfxrtDir},
                    'JFXRT_HOME in System Environment': {System.env['JFXRT_HOME']},
                    'JAVA_HOME in System Environment': {System.env['JAVA_HOME']},
                    'java.home in JVM properties': {System.properties['java.home']}
                 ],
                ['ant-javafx.jar', 'lib/ant-javafx.jar', '../lib/ant-javafx.jar'],
                'JavaFX Packager Tools')
    }
}