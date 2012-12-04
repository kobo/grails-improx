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

import grails.build.logging.GrailsConsole

import org.codehaus.groovy.grails.cli.interactive.InteractiveMode
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

    static void withSocketOutputStreamAsOutAndErr(Socket socket, Closure closure) {
        // Save originals
        def originals = [
            in: System.in,
            out: System.out,
            err: System.err,
            grailsConsole: GrailsConsole.instance
        ]
        try {
            // this is important to make the stdout output to socket instead of console.
            GrailsConsole.instance = SimpleGrailsConsole.getInstance(socket.outputStream)

            // GrailsScriptRunner use the cached console instance.
            InteractiveMode.current.scriptRunner.console = GrailsConsole.instance

            // Replace System.xxx
            System.in = socket.inputStream
            System.out = new GrailsConsolePrintStream(socket.outputStream)
            System.err = new GrailsConsoleErrorPrintStream(socket.outputStream)

            // Invoke
            closure.call()

        } finally {
            // Restore
            System.in = originals.in
            System.out = originals.out
            System.err = originals.err
            GrailsConsole.instance = originals.grailsConsole
            InteractiveMode.current.console = originals.grailsConsole
            InteractiveMode.current.scriptRunner.console = originals.grailsConsole
        }
    }
}
