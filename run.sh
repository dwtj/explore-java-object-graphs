#!/bin/bash -

MESSAGE_PATTERN='^--- |^~~~ |^>>> |^!!! |^[ERROR] '

mvn exec:java -Dexec.mainClass="me.dwtj.Main" | egrep "$MESSAGE_PATTERN"
