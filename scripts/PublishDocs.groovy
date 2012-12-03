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
