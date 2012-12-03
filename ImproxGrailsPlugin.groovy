class ImproxGrailsPlugin {

    def version = "0.1-SNAPSHOT"
    def grailsVersion = "2.1 > *"
    def dependsOn = [:]
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "grails-app/conf/codenarc.groovy",
        "src/docs/**",
    ]
    def title = "Improx - Interactive Mode Proxy"
    def author = "Yasuharu NAKANO"
    def authorEmail = "ynak@jggug.org"
    def license = "APACHE"
    def description = '''\
This plugin provides the way of using interactive mode from other process via TCP.
'''
    def documentation = "http://kobo.github.com/grails-improx/"
    def scm = [url: "https://github.com/kobo/grails-improx"]
    def issueManagement = [system: "GitHub Issues", url: "https://github.com/kobo/grails-improx/issues"]

}
