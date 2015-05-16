#!/bin/bash

# Build our jar file
cd project.latex.balloon
ant jar

ipAddress=192.168.0.7

# Copy the built jar over to the Pi
scp dist/project.latex.balloon.jar pi@$ipAddress:/home/pi/project-latex/project.latex.balloon/dist
scp beans.xml pi@$ipAddress:/home/pi/project-latex/project.latex.balloon/dist
scp logger.properties pi@$ipAddress:/home/pi/project-latex/project.latex.balloon/dist
scp telemetryKeys.json pi@$ipAddress:/home/pi/project-latex/project.latex.balloon/dist
scp -r dist/lib pi@$ipAddress:/home/pi/project-latex/project.latex.balloon/dist