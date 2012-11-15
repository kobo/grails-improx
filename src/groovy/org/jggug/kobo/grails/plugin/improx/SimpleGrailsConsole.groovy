package org.jggug.kobo.grails.plugin.improx

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

    static SimpleGrailsConsole getInstance(OutputStream outputStream) {
        // Replace an OutputStream in PrintWriter.
        // GrailsConsole has getOut(), so direct assignment to 'out' field
        // causes the error which is violation to a read-only field.
        //instance.out.out = new PrintStream(outputStream)
        INSTANCE.out.out = new PrintStream(outputStream)
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