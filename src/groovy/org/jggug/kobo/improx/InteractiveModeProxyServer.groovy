package org.jggug.kobo.improx

import grails.build.logging.GrailsConsole
import org.codehaus.groovy.grails.cli.interactive.InteractiveMode
import java.util.regex.Matcher

/**
 * Listen a port and execute a command specified by a client.
 */
@Singleton
class InteractiveModeProxyServer {

    private static final int DEFAULT_PORT = 8081

    private ServerSocket serverSocket
    private int port

    synchronized void start() {
        if (serverSocket) {
            System.err.println("Interactive-mode proxy server is already running on $port port.")
            return
        }
        try {
            port = resolvePort()
            serverSocket = startServer(port)
        }
        catch (BindException e) {
            System.err.println("$port port is already in use by another process.")
        }
        catch (Throwable e) {
            System.err.println("Failed to invoke interactive-mode proxy server on $port port.")
            e.printStackTrace()
        }
    }

    synchronized void stop() {
        if (!serverSocket) {
            System.err.println "Interactive-mode proxy server hasn't started yet."
            return
        }
        serverSocket.close() // to cause SocketException intentionally
        serverSocket = null
    }

    private static ServerSocket startServer(int port) throws BindException {
        def serverSocket = new ServerSocket(port)
        Thread.start {
            handleRequest(serverSocket)
        }
        println "Interactive-mode proxy server has started on $port port."
        return serverSocket
    }

    private static void handleRequest(ServerSocket serverSocket) {
        try {
            // handle each request sequentially because parallel running must cause a race condition.
            while (true) {
                def socket = serverSocket.accept()
                executeCommand(socket)
            }
        } catch (SocketException e) {
            if (!e.message.startsWith("Socket closed")) {
                e.printStackTrace()
            }
        } finally {
            println "Interactive-mode proxy server stopped."
        }
    }

    private static void executeCommand(Socket socket) {
        def command = retrieveCommand(socket)

        // the before/after 'flush' need to control a line separator rightly on interactive-mode console.
        GrailsConsole.instance.flush()
        try {
            println "${command ? "'$command'" : "<empty>"} (Received from port ${socket.port})"
            IOUtils.withSocketOutputStreamAsOutAndErr(socket) {
                try {
                    if (!command) {
                        System.err.println("ERROR: Command not found.")
                        return
                    }
                    InteractiveMode.current?.parseAndExecute(command)
                } catch (e) {
                    System.err.println("ERROR: Failed to execute command: ${command}")
                    e.printStackTrace()
                }
            }
        } finally {
            socket?.close()
            GrailsConsole.instance.flush()
        }
    }

    private static String retrieveCommand(Socket socket) {
        def rawCommand = IOUtils.readLine(socket.inputStream)?.trim()

        // Support for http client
        if (rawCommand ==~ 'GET /(.*) HTTP/[0-9.]+') {
            def commandFromUrl = URLDecoder.decode(Matcher.lastMatcher.group(1))
            if (commandFromUrl ==~ 'favicon.*') {
                return null // to ignore without error
            }
            return commandFromUrl
        }

        return rawCommand
    }

    private static int resolvePort() {
        return (System.getProperty("improx.port") ?: DEFAULT_PORT) as int
    }

}
