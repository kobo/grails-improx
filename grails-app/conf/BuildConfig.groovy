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

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
    }
    dependencies {
    }

    plugins {
        build(":release:2.0.4") {
            export = false
        }
        test(":codenarc:0.17",
            ":code-coverage:1.2.5",
            ":gmetrics:0.3.1") {
            export = false
        }
    }
}

// codenarc configuration
codenarc {
    reports = {
        MyXmlReport('xml') {                    // The report name "MyXmlReport" is user-defined; Report type is 'xml'
            outputFile = 'target/CodeNarc-Report.xml'  // Set the 'outputFile' property of the (XML) Report
            title = 'Hurv CodeNarc Report'             // Set the 'title' property of the (XML) Report
        }
        MyHtmlReport('html') {                  // Report type is 'html'
            outputFile = 'target/CodeNarc-Report.html'
            title = 'Hurv CodeNarc Report'
        }
    }
    ruleSetFiles = "file:grails-app/conf/codenarc.groovy"
    processTestUnit = false
    processTestIntegration = false
    extraIncludeDirs = ['grails-app/jobs']
}

//cobertura exclusions configuration
coverage {
    xml = true
    exclusionListOverride = [
        "BootStrap*",
        "Config*",
        "BuildConfig*",
        "DataSource*",
        "codenarc*",
        "ApplicationResources*",
        "UrlMappings*",
        "resources*",
        '**/twitter/bootstrap/scaffolding/**'
    ]
}
