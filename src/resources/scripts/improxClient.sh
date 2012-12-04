#!/bin/bash

#
# Usage: improxClient.sh <COMMAND_LINE>
#
# Examples:
#     improxClient.sh help
#     improxClient.sh test-app unit: sample.SampleUnitTests
#     env IMPROX_PORT=8888 improxClient.sh help
#

#---------------------------------------
# Config
#

IMPROX_PORT=${IMPROX_PORT:=8096}

#---------------------------------------
# Definition
#

error() {
    local message="$*"
    echo "ERROR: $message" 1>&2
}

die() {
    local message="$*"
    error "$message"
    exit 1
}

call_improx() {
    local command="$*"
    check_port
    echo "Executing '${command}' via interactive mode proxy..."
    exec 5<> /dev/tcp/localhost/$IMPROX_PORT
    echo "$command" >&5
    /bin/cat <&5
}

check_port() {
    # do check only if there is nc command.
    # there is no nc command on cygwin of windows, but call_improx should be executed.
    # so nc command is used only for checking whether the port is available.
    if which nc > /dev/null 2>&1; then
        nc -z localhost $IMPROX_PORT > /dev/null 2>&1
        [ $? -eq 0 ] || die "\
Failed to connect to server via port $IMPROX_PORT
  Before connecting, install 'improx' plugin into your application, and
  run the 'improx-start' command on interactive mode of the application."
    fi
}

usage() {
    echo "usage: `basename $0` <COMMAND>" >&2
    echo "examples:"
    echo "  $ `basename $0` help" >&2
    echo "  $ improxClient.sh test-app unit: sample.SampleUnitTests" >&2
    echo "  $ env IMPROX_PORT=8888 `basename $0` help" >&2
}

#---------------------------------------
# Main
#

if [ $# -eq 0 ]; then
    usage
    exit 1
fi

call_improx $*

