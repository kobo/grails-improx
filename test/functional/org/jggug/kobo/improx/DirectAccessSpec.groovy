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

class DirectAccessSpec extends AbstractEndToEndSpec {

    private static final int DEFAULT_PORT = 8096

    @Override
    String executeCommand(command) {
        return withSocket("localhost", DEFAULT_PORT) { socket ->
            socket << command << "\n"
            return socket.inputStream.text
        }
    }

    private static withSocket(String host, int port, Closure closure) {
        def socket = null
        try {
            socket = new Socket(host, port)
            return closure.call(socket)
        } finally {
            socket?.close()
        }
    }
}
