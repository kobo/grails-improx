package org.jggug.kobo.grails.plugin.improx

import grails.build.logging.GrailsConsole
import org.codehaus.groovy.grails.cli.logging.GrailsConsoleErrorPrintStream
import org.codehaus.groovy.grails.cli.logging.GrailsConsolePrintStream

class IOUtils {

    static readLine(InputStream ins) {
        def out = new ByteArrayOutputStream()
        int ch
        while ((ch = ins.read()) != -1) {
            if (ch == '\n') { // LF (fixed)
                break
            }
            out.write((byte) ch)
        }
        return out.toString()
    }

    // TODO Refactoring
    static void withSocketOutputStreamAsOutAndErr(Socket socket, Closure closure) {
        def originals = [
            in: System.in,
            out: System.out,
            err: System.err,
            grailsConsoleOut: GrailsConsole.instance.out
        ]

        System.in = socket.inputStream
        def baseOut = new PrintStream(socket.outputStream)
        System.out = new GrailsConsolePrintStream(baseOut)
        System.err = new GrailsConsoleErrorPrintStream(baseOut)
        GrailsConsole.instance.with {
            out = baseOut
            ansiEnabled = false
            userInputActive = false // FIXME Hack to hide a prompt string each line.
        }

        try {
            closure.call()
        } finally {
            System.in = originals.in
            System.out = originals.out
            System.err = originals.err
            GrailsConsole.instance.with {
                out = originals.grailsConsoleOut
                ansiEnabled = true
            }
        }

    }
}
