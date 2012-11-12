package mbean

import org.springframework.jmx.export.annotation.ManagedAttribute
import org.springframework.jmx.export.annotation.ManagedResource
import org.codehaus.groovy.grails.cli.interactive.InteractiveMode


@ManagedResource
class InteractiveProxyBean {

    @ManagedAttribute
    void setCommand(String command) {
        println "(JMX) > $command"
        InteractiveMode.current?.parseAndExecute(command)
    }

}
