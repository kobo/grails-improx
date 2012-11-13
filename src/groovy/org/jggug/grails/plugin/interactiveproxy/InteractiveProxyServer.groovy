package org.jggug.grails.plugin.interactiveproxy

import org.codehaus.groovy.grails.cli.interactive.InteractiveMode
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Listen a port and execute a command specified by a client.
 */
@Singleton
class InteractiveProxyServer {

    private static final int DEFAULT_PORT = 8081
    private static final int TIMEOUT = 1000

    private ServerSocket serverSocket
    private int port

    private AtomicBoolean available = new AtomicBoolean(false)

    void start() {
        if (available.get()) {
            System.err.println("Interactive proxy server is already running on port $port.")
            return
        }
        try {
            available.set(true)
            startServer()
        }
        catch (Throwable e) {
            System.err.println("Failed to invoke InteractiveProxyServer.")
            e.printStackTrace()
        }
    }

    void stop() {
        if (!available.get()) {
            System.err.println "Interactive proxy server is already stopped."
            return
        }
        available.set(false)
    }

    private void startServer() {
        port = getPortNumber()
        serverSocket = new ServerSocket(port)
        serverSocket.setSoTimeout(TIMEOUT)
        Thread.start {
            handleRequest()
        }
        println "Interactive proxy server started with port ${port}."
    }

    // TODO state management is not good enough
    private void handleRequest() {
        try {
            while (available.get()) {
                try {
                    def socket = serverSocket.accept()
                    executeCommand(socket)

                } catch (SocketTimeoutException e) {
                    // do nothing
                }
            }
        } finally {
            serverSocket?.close()
            println "Interactive proxy server stopped."
        }
    }

    private void executeCommand(Socket socket) {
        def command = retrieveCommand(socket)
        if (!command) {
            System.err.println("Command not found.")
            return
        }
        println "Received command '${command}' from port ${socket.port}."
        try {
            IOUtils.withSocketOutputStreamAsOutAndErr(socket) {
                InteractiveMode.current?.parseAndExecute(command)
            }
        } catch (e) {
            System.err.println("Failed to execute command: ${command}")
            e.printStackTrace()
        } finally {
            socket?.close()
        }
    }

    private String retrieveCommand(Socket socket) {
        return IOUtils.readLine(socket.inputStream)?.trim()
    }

    private static int getPortNumber() {
        return (System.getProperty("interactive.proxy.port") ?: DEFAULT_PORT) as int
    }



}
