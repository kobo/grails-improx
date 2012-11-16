class ImproxGrailsPlugin {

    def version = "0.1-SNAPSHOT"
    def grailsVersion = "2.1 > *"
    def dependsOn = [:]
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def title = "Interactive-mode Proxy Plugin"
    def author = "Yasuharu NAKANO"
    def authorEmail = "ynak@jggug.org"
    def license = "APACHE"
    def description = '''\
This plugin can make a CUI script invoke any grails command by communication grails process invoked as interactive-mode in advance.
'''
    def documentation = "http://grails.org/plugin/improx"

}
