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

abstract class AbstractSmartInvokerSpec extends Specification {

    def env = ["IMPROX_DEBUG=true", "PATH=/bin:/usr/bin"]

    def "a file under 'test/unit' directory is invoked as unit test via improx"() {
        given:
        def targetFile = toAbsolutePath("test/unit/org/jggug/kobo/improx/test/SampleUnitTests.groovy")

        when:
        def result = invokeFile(targetFile, env)

        then:
        result =~ "Executing 'test-app -echoOut -echoErr unit: org.jggug.kobo.improx.test.SampleUnitTests' via interactive mode proxy..."
        result =~ "Tests PASSED"
        result =~ "INVOKING UNIT TEST SUCCEED"
    }

    def "a file under 'test/integration' directory is invoked as integration test via improx"() {
        given:
        def targetFile = toAbsolutePath("test/integration/org/jggug/kobo/improx/test/SampleIntegrationTests.groovy")

        when:
        def result = invokeFile(targetFile, env)

        then:
        result =~ "Executing 'test-app -echoOut -echoErr integration: org.jggug.kobo.improx.test.SampleIntegrationTests' via interactive mode proxy..."
        result =~ "Tests PASSED"
        result =~ "INVOKING INTEGRATION TEST SUCCEED"
    }

    def "a file under 'test/functional' directory is invoked as functional test on new grails process"() {
        given:
        def targetFile = toAbsolutePath("test/functional/org/jggug/kobo/improx/test/SampleFunctionalTests.groovy")

        when:
        def result = invokeFile(targetFile, env)

        then:
        result =~ "Executing 'grails test-app -echoOut -echoErr functional: org.jggug.kobo.improx.test.SampleFunctionalTests'..."
    }

    def "a file not under 'test/(unit|integration|functional)' directory is invoked as groovy script"() {
        given:
        def targetFile = toAbsolutePath("test/resources/sampleGroovyScript.groovy")

        and: "to use normal groovy"
        env = ["IMPROX_DEBUG=true", "PATH=/bin:/usr/bin:${toAbsolutePath('test/resources/bin_groovy')}"]

        when:
        def result = invokeFile(targetFile, env)

        then:
        result =~ "Executing 'groovy /.*/test/resources/sampleGroovyScript.groovy'..."
    }

    def "a file not under 'test/(unit|integration|functional)' directory is invoked as groovyclient script"() {
        given:
        def targetFile = toAbsolutePath("test/resources/sampleGroovyScript.groovy")

        and: "to use GroovyServ's groovyclient"
        env = ["IMPROX_DEBUG=true", "PATH=/bin:/usr/bin:${toAbsolutePath('test/resources/bin_groovyclient')}"]

        when:
        def result = invokeFile(targetFile, env)

        then:
        result =~ "Executing 'groovyclient /.*/test/resources/sampleGroovyScript.groovy'..."
    }

    abstract String invokeFile(targetFile, List env)

    private static toAbsolutePath(relativePath) {
        new File(System.properties["user.dir"], relativePath).absolutePath
    }
}
