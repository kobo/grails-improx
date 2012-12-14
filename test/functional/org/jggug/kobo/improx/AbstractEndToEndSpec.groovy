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
