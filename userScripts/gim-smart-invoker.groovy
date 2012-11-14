//==============================
// Grails/Groovy Smart Runner
//==============================

//
// Supported types:
//
// [grails-interactive-proxy]
//    If target *.groovy file under Grails' project directory,
//    it's executed by grails test runner via interactive proxy.
//
// [GroovyServ]
//    If GroovyServ is installed, target *.groovy is executed by GroovyServ.
//
// [Groovy]
//    The target *.groovy is executed by Groovy.
//

//---------
// Helper
//---------

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

class GrailsInteractiveProxyInvoker {
    boolean invoke(File file) {
        def testType = testType(file)
        if (!testType) return false

        def fqcn = fullQualifiedClassName(file)
        invokeViaInteractiveProxy(['test-app', testType , fqcn].join(' '))
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

    private invokeViaInteractiveProxy(command) {
        // TOOO
        println command
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
            if (e.message =~ /^Cannot run program/) return false
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
            new GrailsInteractiveProxyInvoker(),
            new SimpleInvoker(exec: 'groovyclient'),
            new SimpleInvoker(exec: 'groovy'),
            new NotFoundInvoker(),
        ].find { it.invoke(file) }
    }
}

//---------
// Main
//---------

def path = args ? args[0] : '<NO_ARGUMENT>'
new ChainOfInvokers().invoke(groovyFileOf(path))

// TODO Windows path

