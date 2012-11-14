import org.codehaus.groovy.grails.cli.interactive.InteractiveMode

includeTargets << grailsScript("Compile")

target(main: "Start interactive-mode proxy server") {
    depends "compile"

    if (!InteractiveMode.active) {
        System.err.println "You should run this scirpt on interactive-mode."
        return
    }
    def server = classLoader.loadClass("org.jggug.kobo.grails.plugin.improx.InteractiveModeProxyServer", true)
    server.instance.start()
}

setDefaultTarget(main)
