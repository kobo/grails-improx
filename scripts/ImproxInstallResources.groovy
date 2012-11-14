includeTargets << grailsScript('_GrailsInit')

target(main: 'Installs the improxy resources') {
    def targetDir = "${basedir}/improx-resources"
    ant.copy(todir: targetDir) {
        fileset dir: "${improxPluginDir}/src/resources"
    }
    event "StatusUpdate", ["Improx resources installed successfully: ${targetDir}"]
}

setDefaultTarget(main)
