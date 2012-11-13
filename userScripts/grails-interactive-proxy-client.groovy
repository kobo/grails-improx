int DEFAULT_PORT = 8081

String command = args.join(' ')
if (command.trim().empty) {
    System.err.println "ERROR: Command not found."
    System.exit 1
}

int port = (System.getProperty("interactive.proxy.port") ?: DEFAULT_PORT) as int
def socket
try {
    socket = new Socket("localhost", port)
} catch (ConnectException e) {
    System.err.println "ERROR: Failed to connect to server via port $port."
    System.err.println " Install grails-interactive-proxy plugin into your application"
    System.err.println " and invoke start-interactive-proxy before connecting."
    System.exit 1
}

// Send command
socket << command << "\n"

// Output result
socket.withStreams { ins, out ->
    while (true) {
        def (text, eof) = readLine(ins)
        println text
        if (eof) {
            System.exit 0
        }
    }
}

def readLine(InputStream ins) {
    def out = new ByteArrayOutputStream()
    int ch
    while ((ch = ins.read()) != -1) {
        if (ch == '\n') { // LF (fixed)
            return [out.toString(), false] // 2nd arg is EOF flag
        }
        out.write((byte) ch)
    }
    return [out.toString(), true] // 2nd arg is EOF flag
}
