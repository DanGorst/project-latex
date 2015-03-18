#!/bin/bash

# Build our jar file
cd project.latex.balloon
ant jar

# Copy the built jar over to the Pi
scp dist/project.latex.balloon.jar pi@192.168.0.7:/home/pi/project-latex/project.latex.balloon/dist