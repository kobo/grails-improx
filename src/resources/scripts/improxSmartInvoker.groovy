//
// usage: groovy improx-smart-invoker.groovy <TARGET_FILE_PATH>
//        groovyclient improx-smart-invoker.groovy <TARGET_FILE_PATH>  (RECOMMENDED)
//
// This script try to invoke TARGET_FILE_PATH by the following invokers.
//
// [grails-interactive-mode-proxy]
//    If a target *.groovy file under a test directory of a Grails' project,
//    it's executed by grails's test-app with appropriate test type via interactive-mode proxy.
//
// [GroovyServ]
//    If GroovyServ is installed, a target *.groovy is executed by groovyclient of GroovyServ.
//
// [Groovy]
//    A target *.groovy is executed by Groovy.
//

//---------------------------------------
// Definition
//

def groovyFileOf = { String path ->
    def file = new File(path)
    if (!file.exists()) {
        System.err.println "ERROR: File not found: ${path}"
        System.exit 1
    }
    if (!(file.name =~ /.*\.groovy$/)) {
        System.err.println "ERROR: Not groovy file: ${path}"
        System.exit 1
    }
    return file
}

class InteractiveModeProxyInvoker {
    boolean invoke(File file) {
        def testType = testType(file)
        if (!testType) return false

        def fqcn = fullQualifiedClassName(file)
        invokeViaProxy(['test-app', testType , fqcn].join(' '))
        return true
    }

    private testType(File file) {
        switch (file.absolutePath) {
            case ~'.*/test/unit/.*':
                return 'unit:'
            case ~'.*/test/integration/.*':
                return 'integration:'
            case ~'.*/test/functional/.*':
                System.err.println "ERROR: Functional test not supported: ${file}"
                System.exit 1
            default:
                return null
        }
    }

    private fullQualifiedClassName(File file) {
        def relativePathFromPackageRoot = file.absolutePath.replaceFirst('.*/test/.*?/', '')
        def className = (relativePathFromPackageRoot - '.groovy').replaceAll('/', '.')
        return className
    }

    private invokeViaProxy(command) {
        new InteractiveModeProxyClient().invoke(command)
    }
}

class SimpleInvoker {
    def exec

    boolean invoke(File file) {
        try {
            def proc = [exec, file.absolutePath].execute()
            proc.waitFor()
            printIfNotEmpty proc.in.text
            printIfNotEmpty proc.err.text
            return true
        } catch (IOException e) {
            if (e.message.startsWith('Cannot run program')) return false
            throw e
        }
    }

    private printIfNotEmpty(text) {
        if (text.trim()) print text
    }
}

class NotFoundInvoker {
    boolean invoke(File file) {
        System.err.println "ERROR: Cannot invoke: ${file}"
        System.exit 1
    }
}

class ChainOfInvokers {
    boolean invoke(File file) {
        [
            new InteractiveModeProxyInvoker(),
            new SimpleInvoker(exec: 'groovyclient'),
            new SimpleInvoker(exec: 'groovy'),
            new NotFoundInvoker(),
        ].find { it.invoke(file) }
    }
}

class InteractiveModeProxyClient {

    private static final int DEFAULT_PORT = 8081
    private Socket socket

    def invoke(String command) {
        validate(command)
        connect(port)
        send(command)
        waitForResult()
    }

    private validate(String command) {
        if (command.trim().empty) {
            System.err.println "ERROR: Command not found."
            System.exit 1
        }
    }

    private getPort() {
        (System.getProperty("improx.port") ?: DEFAULT_PORT) as int
    }

    private connect(int port) {
        try {
            socket = new Socket("localhost", port)
        } catch (ConnectException e) {
            System.err.println "ERROR: Failed to connect to server via port $port."
            System.err.println " Install grails-interactive-mode-proxy plugin into your application"
            System.err.println " and invoke start-interactive-mode-proxy before connecting."
            System.exit 1
        }
    }

    private send(String command) {
        socket << command << "\n"
    }

    private waitForResult() {
        socket.withStreams { ins, out ->
            while (true) {
                def (text, eof) = readLine(ins)
                println text
                if (eof) {
                    System.exit 0
                }
            }
        }
    }

    private readLine(InputStream ins) {
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
}

//---------------------------------------
// Main
//

def path = args ? args[0] : '<NO_ARGUMENT>'
new ChainOfInvokers().invoke(groovyFileOf(path))

