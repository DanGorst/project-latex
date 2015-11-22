#!/bin/bash

# Build our jar file
cd project.latex.balloon
ant jar

ipAddress=$1

# Create necessary directories.
ssh pi@$ipAddress mkdir -p /home/pi/project-latex/dist/encoded-image

# Copy files across
scp ../runOnPi.sh pi@$ipAddress:/home/pi/project-latex
scp -r ../data/pi-cam-images dist/project.latex.balloon.jar beans.xml email.xml goProOnlyBeans.xml logger.properties telemetryKeys.json dist/lib src/scripts ../3rdParty/ssdv pi@$ipAddress:/home/pi/project-latex/dist

