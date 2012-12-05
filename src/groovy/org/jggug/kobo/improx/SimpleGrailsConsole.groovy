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

/**
 * A simple GrailsConsole without extra decorations for environments which doesn't support ANSI.
 * This is singleton because the setup of GrailsConsole needs intentional processes.
 */
class SimpleGrailsConsole extends GrailsConsole {

    private static final NULL_STREAM = new PrintStream(new ByteArrayOutputStream())
    private static final SimpleGrailsConsole INSTANCE

    static {
        // Replace System.out here makes, so that an unnecessary 'println' in the end of
        // constructor of GrailsConsole is ignored.
        System.out = NULL_STREAM

        INSTANCE = new SimpleGrailsConsole()
    }

    static SimpleGrailsConsole getInstance(Socket socket) {
        // Replace an OutputStream in PrintWriter.
        // GrailsConsole has getOut(), so direct assignment to 'out' field
        // causes the error which is violation to a read-only field.
        //instance.out.out = new PrintStream(outputStream)
        INSTANCE.out.out = new PrintStream(socket.outputStream)

        // Replace reader to read the user input from console.
        INSTANCE.reader.in = socket.inputStream

        return INSTANCE
    }

    private SimpleGrailsConsole() {
        // Turn off ansi coloring
        ansiEnabled = false
    }

    @Override
    void error(String label, String message) {
        if (message == null) {
            return
        }
        // simply print a message without label.
        out.println(message);
    }
}
