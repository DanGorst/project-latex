#!/bin/bash

# Build our jar file
cd project.latex.balloon
ant jar

# Copy the built jar over to the Pi
scp dist/project.latex.balloon.jar pi@192.168.0.7:/home/pi/project-latex/project.latex.balloon/dist
scp beans.xml pi@192.168.0.7:/home/pi/project-latex/project.latex.balloon/dist
scp logger.properties pi@192.168.0.7:/home/pi/project-latex/project.latex.balloon/dist
scp telemetryKeys.json pi@192.168.0.7:/home/pi/project-latex/project.latex.balloon/dist