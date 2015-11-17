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
package org.bitbucket.shemnon.javafxplugin

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.util.ConfigureUtil
import org.gradle.api.file.FileCollection

import java.awt.image.BufferedImage

class JavaFXPluginExtension {

    public static final String NO_RUNTIME = '<NO RUNTIME>'

    protected Map<String, Map> profileMap = [:]

    // preliminaries
    FileCollection jfxrtJar
    FileCollection antJavaFXJar
    boolean verbose
    
    // where do we get our source>
    def sourceSet = 'main'

    // build steps
    SigningKeyInfo debugKey
    SigningKeyInfo releaseKey

    String signingMode

    boolean embedLauncher = true

    // app info
    String id
    String appID
    String appName

    String packaging

    // JNLP Packaging
    int width = 1024
    int height = 768
    boolean embedJNLP = false
    String updateMode = "background"
    boolean offlineAllowed = true
    String codebase

    // runtime stuff
    String mainClass
    List<String> jvmArgs = []
    Map<String, String> systemProperties = [:]
    List<String> arguments = []

    String javaRuntime

    // deploy/info attributes
    String category
    String copyright
    String description
    String licenseType
    String vendor

    // deploy/preferences attributes
    Boolean installSystemWide
    boolean menu
    boolean shortcut

    protected List<IconInfo> iconInfos = []
    protected List<IconInfo> getIconInfos() { return iconInfos}
    protected void setIconInfos(List<IconInfo> icons) {iconInfos = icons}

    Map<String, ? super Object> bundleArgumentsMap = [:]

    public debugKey(Closure closure) {
        debugKey = new SigningKeyInfo(closure)
    }

    public releaseKey(Closure closure) {
        releaseKey = new SigningKeyInfo(closure)
    }

    def icon(Closure closure) {
        getIconInfos().add(new IconInfo(closure))
    }

    def icons(Closure closure) {
        Map m = [:]
        ConfigureUtil.configure(closure, m)
        m.each {k, v ->
            if (v instanceof List) {
                v.each {
                    addIcon(k, it)
                }
            } else {
                addIcon(k, v)
            }
        }
    }

    def profiles(Closure closure) {
        ConfigureUtil.configure(closure, new MethodToMap(map: profileMap))
    }

    Map getProfile(String profile) {
        return profileMap.get(profile)
    }

    def bundleArguments(Closure closure) {
        ConfigureUtil.configure(closure, bundleArgumentsMap)
    }

    Map getBundleArguments() {
        return bundleArgumentsMap
    }

    protected void addIcon(String kind, String href) {
        IconInfo ii = new IconInfo(href)
        ii.kind = kind
        getIconInfos().add(ii)
    }
    
    protected getSourceSet(String propertyName, Project project) {
        return resolveSourceSet(this[propertyName], project)
    }
    
    protected resolveSourceSet(SourceSet sourceSet, Project project) {
        return sourceSet
    }
    
    protected resolveSourceSet(String sourceSet, Project project) {
        return project.sourceSets[sourceSet]
    }
}

class SigningKeyInfo {
    String alias
    String keyPass
    File keyStore
    String storePass
    String storeType


    public SigningKeyInfo(Closure configure) {
        ConfigureUtil.configure(configure, this)
    }

    //TODO logic methods
    //   - determine if a key exists
    //   - load stuff like validity and dname from existing key
    //   - prompt for password if set to null on a read
}

class IconInfo {
    String href
    String kind = 'default'
    int width = -1
    int height = -1
    int depth = -1
    double scale = 1 // for retina
    BufferedImage _image
    File file

    public IconInfo(String href) {
        this.href = href
    }

    public IconInfo(Closure configure) {
        ConfigureUtil.configure(configure, this)
    }
    
    public String toString() {
        "[IconInfo kind='$kind' location='$href' scale=$scale]"
    }

}

class MethodToMap {

    Map map

    Object methodMissing(String name, args) {
        if (args.length == 1 && args[0] instanceof Closure) {
            map[name] = [:]
            ConfigureUtil.configure(args[0], map[name])
        } else {
            throw new MissingMethodException(name, JavaFXPluginExtension.class, args, false);
        }
    }
}
