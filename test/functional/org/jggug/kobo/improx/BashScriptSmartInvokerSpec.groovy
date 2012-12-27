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

class BashScriptSmartInvokerSpec extends AbstractSmartInvokerSpec {

    @Override
    String invokeFile(file) {
        def script = System.properties["user.dir"] + "/build/improx-resources/scripts/improxSmartInvoker.sh"
        def process = [script, file].execute(["IMPROX_DEBUG=true"], new File(System.properties["user.dir"]))
        assert process.err.text == ""
        return process.in.text
    }
}
