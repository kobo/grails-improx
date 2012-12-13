package org.jggug.kobo.improx

import spock.lang.Specification

abstract class AbstractEndToEndSpec extends Specification {

    def "help command"() {
        when:
        def result = executeCommand("help")

        then:
        result =~ "Usage"
        result =~ "wrapper"
    }

    def "test-app unit: Target"() {
        when:
        def result = executeCommand("test-app unit: NotExistsTestClass")

        then:
        result =~ "Tests PASSED"
    }

    def "command not found"() {
        when:
        def stdout = executeCommand('NOT_EXISTS_COMMAND')

        then:
        stdout =~ 'ERROR: Failed to execute command: NOT_EXISTS_COMMAND'
    }

    def "via http"() {
        when:
        def result = executeCommand("GET /help HTTP/1.1")

        then:
        result =~ "Usage"
        result =~ "wrapper"
    }

    abstract String executeCommand(command)
}
