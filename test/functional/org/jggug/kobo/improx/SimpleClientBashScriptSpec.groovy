package org.jggug.kobo.improx

class SimpleClientBashScriptSpec extends AbstractEndToEndSpec {

    @Override
    String executeCommand(command) {
        def script = System.properties["user.dir"] + "/build/improx-resources/scripts/improxClient.sh"
        def process = [script, command].execute()
        assert process.err.text == ""
        return process.in.text
    }
}