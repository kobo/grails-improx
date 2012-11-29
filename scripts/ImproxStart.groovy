import org.codehaus.groovy.grails.cli.interactive.InteractiveMode

includeTargets << grailsScript("Compile")

USAGE = """
    grails [-Dimprox.port=PORT] improx-start

where
    PORT = The port number which is used for connection between client and server.

examples
    grails improx-start
    grails -Dimprox.port=9999 improx-start

optional argument default values
    PORT = 8096
"""

target(main: "Start interactive-mode proxy server") {
    depends "compile"

    if (!InteractiveMode.active) {
        event "StatusError", ["You should run this scirpt on interactive-mode."]
        return
    }
    def server = classLoader.loadClass("org.jggug.kobo.improx.InteractiveModeProxyServer", true)
    server.instance.start()
}

setDefaultTarget(main)

