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

import org.codehaus.groovy.grails.cli.interactive.InteractiveMode

includeTargets << grailsScript("_GrailsCompile")

USAGE = """
    grails [-Dimprox.port=PORT] improx-start

where
    PORT = The port number which is used for connection between client and server.

examples
    grails improx-start
    grails -Dimprox.port=9999 improx-start

optional argument default values
    PORT = 8096
"""

target(improxStart: "Start interactive-mode proxy server") {
    depends "compile"

    if (!InteractiveMode.active) {
        event "StatusError", ["You should run this scirpt on interactive mode."]
        return
    }
    def server = classLoader.loadClass("org.jggug.kobo.improx.InteractiveModeProxyServer", true)
    server.instance.start()
}

setDefaultTarget improxStart
