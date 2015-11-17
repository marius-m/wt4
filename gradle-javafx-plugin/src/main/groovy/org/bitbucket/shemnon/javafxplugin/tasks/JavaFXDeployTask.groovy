/*
 * Copyright (c) 2012, 2014, Danno Ferrin
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
package org.bitbucket.shemnon.javafxplugin.tasks

import com.oracle.tools.packager.Bundlers
import com.oracle.tools.packager.ConfigException
import com.oracle.tools.packager.RelativeFileSet
import com.oracle.tools.packager.UnsupportedPlatformException
import org.gradle.api.file.FileVisitDetails

import static com.oracle.tools.packager.StandardBundlerParam.*
import com.oracle.tools.packager.Log
import net.sf.image4j.codec.bmp.BMPEncoder
import net.sf.image4j.codec.ico.ICOEncoder
import org.bitbucket.shemnon.javafxplugin.IconInfo
import org.bitbucket.shemnon.javafxplugin.JavaFXPluginExtension
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.os.OperatingSystem
import org.gradle.util.ConfigureUtil

import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

class JavaFXDeployTask extends ConventionTask {

    String packaging

    FileCollection antJavaFXJar

    String id
    String appID
    String appName

    boolean verbose = false

    int width = 1024
    int height = 768
    boolean embedJNLP = false
    String updateMode = "background"
    boolean offlineAllowed = true
    String codebase

    String mainClass
    List<String> jvmArgs = []
    Map<String, String> systemProperties = [:]
    List<String> arguments = []

    String javaRuntime

    // deploy/info attributes
    String category
    String copyright
    String deployDescription
    String licenseType
    String vendor
    List<IconInfo> iconInfos = []
    String version

    // deploy/preferences attributes
    Boolean installSystemWide
    boolean menu
    boolean shortcut

    Map<String, ? super Object> bundleArguments = [:]

    @InputFiles
    FileCollection inputFiles

    @InputDirectory
    File resourcesDir

    @OutputDirectory
    File distsDir

    static void putIfNotNull(def params, def param, def value) {
        if (value != null) {
            params[param.ID] = value;
        }
    }

    static void putIfNotEmpty(def params, def param, def value) {
        if (!value.isEmpty()) {
            params[param.ID] = value;
        }
    }

    @TaskAction
    processResources() {
        def params = [:]

        putIfNotNull(params, VERSION, getVersion())

        putIfNotNull(params, IDENTIFIER, getId())
        putIfNotNull(params, APP_NAME, getAppName())
        putIfNotNull(params, MAIN_CLASS, getMainClass())

        File appLayoutDir = new File(project.buildDir, "javapackager-app")
        project.delete(appLayoutDir)
        appLayoutDir.mkdirs();

        project.copy({
            getInputFiles().each { from it }
            into appLayoutDir
        })
        params[CLASSPATH.ID] = new RelativeFileSet(appLayoutDir, project.fileTree(appLayoutDir).files).includedFiles.join(' ')

        // now copy deploy resources
        project.copy({
            project.sourceSets['package'].resources.srcDirs.each { srcDir ->
                if (srcDir.name != 'package') {
                    from srcDir
                }
            }
            into appLayoutDir
        })

        params[APP_RESOURCES.ID] = new RelativeFileSet(appLayoutDir, project.fileTree(appLayoutDir).files)


        putIfNotNull(params, TITLE, getAppName())
        putIfNotNull(params, CATEGORY, getCategory())
        putIfNotNull(params, COPYRIGHT, getCopyright())
        putIfNotNull(params, DESCRIPTION, getDeployDescription())
        putIfNotNull(params, LICENSE_TYPE, getLicenseType())
        putIfNotNull(params, VENDOR, getVendor())

        putIfNotNull(params, SYSTEM_WIDE, getInstallSystemWide())
        putIfNotNull(params, MENU_HINT, getMenu())
        putIfNotNull(params, SHORTCUT_HINT, getShortcut())

        putIfNotEmpty(params, JVM_OPTIONS, [] + getJvmArgs())
        putIfNotEmpty(params, JVM_PROPERTIES, [:] + getSystemProperties())

        //FIXME 8u40 deployParams.arguments = getArguments()

        String runtime = getJavaRuntime()
        if (runtime != null) {
            File rtFile
            if (runtime == JavaFXPluginExtension.NO_RUNTIME) {
                getLogger().info("Java runtime to be bundled: none, bundle will rely on locally installed runtimes")
                rtFile = null
            } else {
                getLogger().info("Java runtime to be bundled: $runtime")
                rtFile = new File(runtime)
                if (!rtFile.exists()) {
                    throw new GradleException("No Java Runtime found at specified runtime path: $runtime")
                }
            }
            params["runtime"] = rtFile.toString();
        } else {
            getLogger().info("Java runtime to be bundled: the runtime executing the Gradle build")
        }

        File packageResourcesOutput = project.sourceSets['package'].output.resourcesDir
        processIcons(packageResourcesOutput)

        // hack
        Bundlers.class.classLoader.addURL(packageResourcesOutput.parentFile.toURI().toURL())


        Log.setLogger(new Log.Logger(getVerbose()) {
            @Override
            void info(String msg) {
                getLogger().info(msg)
            }

            @Override
            void verbose(String msg) {
                if (getVerbose()) {
                    info(msg)
                } else {
                    debug(msg)
                }
            }

            @Override
            void debug(String msg) {
                getLogger().debug(msg)
            }
        } as Log.Logger)

        Set<String> duplicateKeys = new TreeSet<String>(getBundleArguments().keySet())
        duplicateKeys.retainAll(params.keySet())
        if (duplicateKeys) {
            throw new GradleException("The build cannot execute because the bundleArguments duplicates the following keys: $duplicateKeys")
        }

        params.putAll(getBundleArguments());

        def outDir  = getDistsDir()
        def bundleType = getPackaging().toLowerCase()

        Bundlers bundlers = Bundlers.createBundlersInstance() // service discovery?
        Log.debug "Target bundle type is '$bundleType'"
        bundlers.bundlers.each { b ->
            def localParams = [:] + params
            try {
                Log.debug "Considering $b.name which is a '${b.bundleType.toLowerCase()}' "
                //noinspection deprecation
                if (bundleType != null
                        && "all" != bundleType
                        && b.ID.toLowerCase() != bundleType
                        && b.bundleType.toLowerCase() != bundleType
                )
                {
                    // not this kind of bundler
                    Log.debug "skipping $b.name because of bundleType mismatch"
                    return;
                }

                if (b.validate(localParams)) {
                    Log.debug("$b.name passed validaiton - executing")
                    b.execute(localParams, outDir);
                }
            } catch (UnsupportedPlatformException ignore) {
                // quietly ignore
            } catch (ConfigException e) {
                Log.info """Skipping $b.name because of configuration error $e.message
Advice to Fix: $e.advice"""
            }
        }

        Log.setLogger(null)
    }

    def icon(Closure closure) {
        getIconInfos().add(new IconInfo(closure))
    }

    def icons(Closure closure) {
        Map m = [:]
        ConfigureUtil.configure(closure, m)
        m.each {String k, def v ->
            if (v instanceof List) {
                v.each {String s ->
                    addIcon(k, s)
                }
            } else {
                addIcon(k, v as String)
            }
        }
    }

    protected void addIcon(String kind, String href) {
        IconInfo ii = new IconInfo(href)
        ii.kind = kind
        getIconInfos().add(ii)
    }

    protected void loadConventionalIcons(String kind) {
        // this convention is non-configurable, hence it gets hard coded
        project.fileTree('src/deploy/package', { include "$kind*.png"}).visit { FileVisitDetails fileDetails ->
            addIcon(kind, fileDetails.path)
        }
    }

    protected void processIcons(File destination) {
        if (getIconInfos().isEmpty()) {
            // if nothing is configured, use the convention
            loadConventionalIcons('shortcut')
            loadConventionalIcons('volume')
            loadConventionalIcons('setup')
        }
        if (OperatingSystem.current().isMacOsX()) {
            processMacOSXIcons(destination);
        }
        if (OperatingSystem.current().isWindows()) {
            processWindowsIcons(destination);
        }
        if (OperatingSystem.current().isLinux()) {
            processLinuxIcons(destination)
        }
    }

    protected void processMacOSXIcons(File destination) {
        processMacOSXIcns('shortcut',
                new File(destination, "macosx/${project.javafx.appName}.icns"))
        processMacOSXIcns('volume',
                new File(destination, "macosx/${project.javafx.appName}-volume.icns"))
    }

    def macIcnsSizes = [16,32,128,256,512]
    protected void processMacOSXIcns(String kind, File iconLocation) {
        // get explicit
        def dest = "$project.buildDir/icons/${kind}.iconset"
        project.mkdir(dest)
        boolean createIcon = false
        for (IconInfo ii : getIconInfos()) {
            if (kind == ii.kind) {
                BufferedImage icon = getImage(ii)
                if (icon == null) {
                    logger.error("Icon $ii.href for $ii.kind rejected from MacOSX bundling because $ii.href does not exist or it is not an image.")
                    continue;
                }
                if (ii.width != ii.height) {
                    logger.info("Icon $ii.href for $ii.kind rejected from MacOSX bundling because it is not square: $ii.width x $ii.height.")
                    continue;
                }
                if (ii.scale != 1 && ii.scale != 2) {
                    logger.info("Icon $ii.href for $ii.kind rejected from MacOSX bundling because it has an invalid scale.")
                    continue;
                }
                if (!macIcnsSizes.contains(ii.width)) {
                    logger.info("Icon $ii.href for $ii.kind rejected from MacOSX bundling because it is an unsupported dimension ($ii.width x $ii.height).  $macIcnsSizes square dimensions are supported.")
                    continue;
                }

                ant.copy(file: ii.file, toFile: "$dest/icon_${ii.width}x${ii.height}${ii.scale == 2 ? '@2x': ''}.png")
                createIcon = true
            }

        }
        if (createIcon) {
            project.exec {
                executable 'iconutil'
                args ('--convert', 'icns', dest)
            }
            ant.copy(file: "$project.buildDir/icons/${kind}.icns", toFile: iconLocation)
        } else {
            logger.info("Skipped icon " + kind + " because no icon info entries were found.")
        }
    }

    protected void processWindowsIcons(File destination) {
        processWindowsIco('shortcut',
                new File(destination, "windows/${project.javafx.appName}.ico"))
        processWindowsBMP('setup',
                new File(destination, "windows/${project.javafx.appName}-setup-icon.bmp"))
    }

    protected void processWindowsBMP(String kind, File destination) {
        boolean processed = false
        for (IconInfo ii : getIconInfos()) {
            if (kind == ii.kind) {
                BufferedImage icon = getImage(ii)
                if (icon == null) {
                    logger.error("Icon $ii.href for $ii.kind rejected from Windows bundling because $ii.href does not exist or it is not an image.")
                    continue;
                }
                if (processed) {
                    logger.info("Icon $ii.href for $ii.kind rejected from Windows bundling because only one icon can be used.")
                    continue;
                }

                double scale = Math.min(Math.min(55.0 / icon.width, 58.0 / icon.height), 1.0)

                BufferedImage bi = new BufferedImage((int)icon.width*scale, (int)icon.height*scale, BufferedImage.TYPE_INT_ARGB)
                def g = bi.graphics
                def t = new AffineTransform()
                t.scale(scale, scale)
                g.transform = t
                g.drawImage(icon, 0, 0, null)
                BMPEncoder.write(bi, destination)
                processed = true
            }
        }
    }

    protected void processWindowsIco(String kind, File destination) {
        Map<Integer, BufferedImage> images = new TreeMap<Integer, BufferedImage>()
        for (IconInfo ii : getIconInfos()) {
            if (kind == ii.kind) {
                BufferedImage icon = getImage(ii)
                if (icon == null) {
                    logger.error("Icon $ii.href for $ii.kind rejected from Windows bundling because $ii.href does not exist or it is not an image.")
                    continue;
                }
                if (ii.scale != 1) {
                    logger.info("Icon $ii.href for $ii.kind rejected from Widnows bundling because it has a scale other than '1'.")
                    continue;
                }


                if (icon.width != icon.height) {
                    logger.info("Icon $ii.href for $ii.kind rejected from Windows bundling because it is not square: $icon.width x $icon.height.")
                    continue;
                }
                BufferedImage bi = new BufferedImage(icon.width, icon.height, BufferedImage.TYPE_INT_ARGB)
                bi.graphics.drawImage(icon, 0, 0, null)
                images.put(bi.width, bi)
            }

        }
        if (images) {
            destination.parentFile.mkdirs()
            List<BufferedImage> icons = (images.values() as List).reverse()
            ICOEncoder.write(icons, destination)
        }
    }

    protected void processLinuxIcons(File destination) {
        IconInfo largestIcon = null
        for (IconInfo ii : getIconInfos()) {
            if ('shortcut' == ii.kind) {
                BufferedImage icon = getImage(ii)
                if (icon == null) {
                    logger.error("Icon $ii.href for $ii.kind rejected from Linux bundling because $ii.href does not exist or it is not an image.")
                    continue;
                }
                if (largestIcon?.width < ii.width) {
                    if (largestIcon != null) {
                        logger.info("Icon $largestIcon.href for $largestIcon.kind rejected from Linux bundling because it is not the largest icon.")
                    }
                    largestIcon = ii
                } else {
                    logger.info("Icon $ii.href for $ii.kind rejected from Linux bundling because it is not the largest icon.")
                }
            }
        }

        if (largestIcon) {
             ant.copy(file: largestIcon.file, toFile: new File(destination, "linux/${project.javafx.appName.replaceAll('\\s', '')}.png"))
        }

    }

    BufferedImage getImage(IconInfo ii) {
        if (ii._image == null) {
            ii.file = getProject().file(ii.href)
            if (!ii.file.file) {
                // try to resolve relative to output
                ii.file = new File(getResourcesDir(), ii.href)
            }
            if (!ii.file.file) return null

            ii._image = ImageIO.read(ii.file)

            if (ii.scale == 2 || ii.href.contains('@2x')) {
                ii.width = ii._image.width / 2
                ii.height = ii._image.height / 2
                ii.scale = 2
            } else {
                ii.width = ii._image.width
                ii.height = ii._image.height
                ii.scale = 1
            }
        }
        return ii._image
    }
}
