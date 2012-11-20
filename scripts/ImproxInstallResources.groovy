includeTargets << grailsScript('_GrailsInit')

USAGE = """
    grails improx-install-resources
"""

target(main: 'Installs the improxy resources into the project directory') {
    def targetDir = "${basedir}/improx-resources"
    ant.copy(todir: targetDir) {
        fileset dir: "${improxPluginDir}/src/resources"
    }
    event "StatusUpdate", ["Improx resources installed successfully: ${targetDir}"]
}

setDefaultTarget(main)
