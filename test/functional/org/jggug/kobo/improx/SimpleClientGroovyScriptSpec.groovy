package org.jggug.kobo.improx

class SimpleClientGroovyScriptSpec extends AbstractEndToEndSpec {

    @Override
    String executeCommand(command) {
        def script = System.properties["user.dir"] + "/build/improx-resources/scripts/improxClient.groovy"
        def process = ["groovy", script, command].execute()
        return process.in.text
    }
}