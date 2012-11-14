import org.codehaus.groovy.grails.cli.interactive.InteractiveMode

includeTargets << grailsScript("Compile")

target(main: "Start interactive proxy server") {
    depends "compile"

    if (!InteractiveMode.active) {
        System.err.println "You should run this scirpt on interactive mode."
        return
    }
    def InteractiveProxyServer = classLoader.loadClass("org.jggug.grails.plugin.interactiveproxy.InteractiveProxyServer", true)
    InteractiveProxyServer.instance.start()
}

setDefaultTarget(main)
