package org.jggug.kobo.grails.plugin.improx

import grails.build.logging.GrailsConsole
import org.codehaus.groovy.grails.cli.interactive.InteractiveMode

/**
 * Listen a port and execute a command specified by a client.
 */
@Singleton
class InteractiveModeProxyServer {

    private static final int DEFAULT_PORT = 8081
    private static final int TIMEOUT = 1000

    private ServerSocket serverSocket
    private int port

    synchronized void start() {
        port = resolvePort()
        if (serverSocket) {
            System.err.println("Interactive-mode proxy server is already running on $port port.")
            return
        }
        try {
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
        if (!command) {
            System.err.println("Command not found.")
            return
        }

        // the before/after 'flush' need to control a line separator rightly on interactive-mode console.
        GrailsConsole.instance.flush()
        println "'${command}' (Received from port ${socket.port})"
        try {
            IOUtils.withSocketOutputStreamAsOutAndErr(socket) {
                try {
                    InteractiveMode.current?.parseAndExecute(command)
                } catch (e) {
                    System.err.println("Failed to execute command: ${command}")
                    e.printStackTrace()
                }
            }
        } finally {
            socket?.close()
            GrailsConsole.instance.flush()
        }
    }

    private static String retrieveCommand(Socket socket) {
        return IOUtils.readLine(socket.inputStream)?.trim()
    }

    private static int resolvePort() {
        return (System.getProperty("improx.port") ?: DEFAULT_PORT) as int
    }

}
