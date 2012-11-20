includeTargets << grailsScript("Compile")

USAGE = """
    grails improx-stop
"""

target(main: "Stop interactive-mode proxy server") {
    depends "compile"

    def server = classLoader.loadClass("org.jggug.kobo.improx.InteractiveModeProxyServer", true)
    server.instance.stop()
}

setDefaultTarget(main)
