#!/bin/bash -

mvn exec:java -Dexec.mainClass="me.dwtj.Main" | grep '>>>>>>'
