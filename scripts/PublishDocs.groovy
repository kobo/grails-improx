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

includeTargets << grailsScript("_GrailsDocs")

target(main: "Build documentation and copy to gh-pages branch") {
    depends(clean, docs)

    executeCommand 'git', ['checkout', 'gh-pages']
    ant.copy(todir: "${basedir}/docs", overwrite: 'yes') {
        fileset dir: "${projectTargetDir}/docs"
    }
}

setDefaultTarget(main)

def executeCommand(command, arguments = []) {
    println ">> Executing command..."
    println "\$ ${command} ${arguments.join(' ')}"
    def p = [command, *arguments].execute()
    p.waitFor()
    println p.text
    println p.err.text
}
