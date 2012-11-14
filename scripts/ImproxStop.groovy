includeTargets << grailsScript("Compile")

target(main: "Stop interactive-mode proxy server") {
    depends "compile"

    def server = classLoader.loadClass("org.jggug.kobo.grails.plugin.improx.InteractiveModeProxyServer", true)
    server.instance.stop()
}

setDefaultTarget(main)
