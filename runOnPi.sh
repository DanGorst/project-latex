#!/bin/bash

# Navigate to the directory where the jar lives
cd dist

# Run it as root, specifying the main class
sudo java -cp project.latex.balloon.jar project.latex.balloon.BalloonController beans.xml
