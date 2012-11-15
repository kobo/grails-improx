package org.jggug.kobo.grails.plugin.improx

import grails.build.logging.GrailsConsole

/**
 * Simple GrailsConsole without extra decorations for environments which doesn't support ANSI.
 */
class SimpleGrailsConsole extends GrailsConsole {

    static createInstance(OutputStream outputStream) {
        // To replace System.out here makes 'println' in the end of constructor
        // of GrailsConsole be ignored.
        System.out = new PrintStream(new ByteArrayOutputStream())
        return new SimpleGrailsConsole(outputStream)
    }

    private SimpleGrailsConsole(OutputStream outputStream) {
        // Replace an OutputStream in PrintWriter.
        // GrailsConsole has getOut(), so direct assignment to 'out' field
        // causes the error which is violation to a read-only field.
        out.out = new PrintStream(outputStream)

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