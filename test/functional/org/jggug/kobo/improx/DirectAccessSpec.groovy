package org.jggug.kobo.improx

class DirectAccessSpec extends AbstractEndToEndSpec {

    private static final int DEFAULT_PORT = 8096

    @Override
    String executeCommand(command) {
        return withSocket("localhost", DEFAULT_PORT) { socket ->
            socket << command << "\n"
            return socket.inputStream.text
        }
    }

    private static withSocket(String host, int port, Closure closure) {
        def socket = null
        try {
            socket = new Socket(host, port)
            return closure.call(socket)
        } finally {
            socket?.close()
        }
    }
}
