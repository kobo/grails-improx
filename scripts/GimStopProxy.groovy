includeTargets << grailsScript("Compile")

target(main: "Stop interactive proxy server") {
    depends "compile"

    def InteractiveProxyServer = classLoader.loadClass("org.jggug.grails.plugin.interactiveproxy.InteractiveProxyServer", true)
    InteractiveProxyServer.instance.stop()
}

setDefaultTarget(main)
