/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jggug.kobo.improx

import org.codehaus.groovy.grails.cli.interactive.InteractiveMode

import java.util.regex.Matcher

/**
 * Listen a port and execute a command specified by a client.
 */
@Singleton
class InteractiveModeProxyServer {

    private static final int DEFAULT_PORT = 8096

    private ServerSocket serverSocket
    private int port

    synchronized void start() {
        if (serverSocket) {
            error("Interactive mode proxy server is already running on $port port.")
            return
        }
        try {
            port = resolvePort()
            serverSocket = startServer(port)
        }
        catch (BindException e) {
            error("$port port is already in use by another process.")
        }
        catch (Throwable e) {
            error("Failed to invoke interactive mode proxy server on $port port.")
            e.printStackTrace()
        }
    }

    synchronized void stop() {
        if (!serverSocket) {
            error "Interactive mode proxy server hasn't started yet."
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
        status "Interactive mode proxy server has started on $port port."
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
            // do nothing because SocketException normally occurs when improx-stop is invoked.
        } finally {
            status "Interactive mode proxy server stopped."
        }
    }

    private static void executeCommand(Socket socket) {
        try {
            def command = retrieveCommand(socket)

            info "${command ? "'$command'" : "<empty>"} (Received from port ${socket.port})"
            IOUtils.withSocketOutputStreamAsOutAndErr(socket) {
                try {
                    if (!command) {
                        error("ERROR: Command not found.")
                        return
                    }
                    InteractiveMode.current?.parseAndExecute(command)
                } catch (Throwable e) {
                    error("ERROR: Failed to execute command: ${command}", e)
                }
            }
        } finally {
            socket?.close()
        }
    }

    private static String retrieveCommand(Socket socket) {
        def rawCommand = IOUtils.readLine(socket.inputStream)?.trim()

        // Support for http client
        if (rawCommand ==~ 'GET /(.*) HTTP/[0-9.]+') {
            def commandFromUrl = URLDecoder.decode(Matcher.lastMatcher.group(1), "UTF-8").trim()
            if (commandFromUrl ==~ /favicon\..*/) {
                return null // to ignore without error
            }
            return commandFromUrl
        }

        return rawCommand
    }

    private static int resolvePort() {
        return (System.getProperty("improx.port") ?: DEFAULT_PORT) as int
    }

    private static void info(String message) {
        IOUtils.ORIGINALS.grailsConsole.append(message)
        IOUtils.ORIGINALS.grailsConsole.showPrompt()
    }

    private static void status(String status) {
        IOUtils.ORIGINALS.grailsConsole.addStatus(status)
    }

    private static void error(String message, Throwable e) {
        System.err.println(message)
        e.printStackTrace()
    }

    private static void error(String message) {
        System.err.println(message)
    }
}
