//======================================================
// Simple Test Runner for Groovy Interactive-mode Proxy
//======================================================

//---------
// Helper
//---------

def testType = { File file ->
    switch (file.absolutePath) {
        case ~'.*/test/unit/.*':
            return 'unit:'
        case ~'.*/test/integration/.*':
            return 'integration:'
        default:
            System.err.println "ERROR: Not test: ${file}"
            System.exit 1
    }
}

def fullQualifiedClassName = { File file ->
    def relativePathFromPackageRoot = file.absolutePath.replaceFirst('.*/test/.*?/', '')
    def className = (relativePathFromPackageRoot - '.groovy').replaceAll('/', '.')
    return className
}

def executeViaInteractiveProxy = { command ->
    // TOOO
    println command.join(' ')
}

//---------
// Main
//---------

def filePath = args[0]
def file = new File(filePath)
if (!file.exists()) {
    System.err.println "ERROR: File not found: ${filePath}"
    System.exit 1
}

def command = ['test-app', testType(file), fullQualifiedClassName(file)]
executeViaInteractiveProxy command
