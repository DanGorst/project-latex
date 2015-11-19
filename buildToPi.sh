#!/bin/bash

# Build our jar file
cd project.latex.balloon
ant jar

ipAddress=$1

# Copy the built jar over to the Pi
scp -r data/encoded-image dist/project.latex.balloon.jar beans.xml email.xml goProOnlyBeans.xml logger.properties telemetryKeys.json dist/lib src/scripts ../3rdParty/ssdv pi@$ipAddress:/home/pi/project-latex/project.latex.balloon/dist
