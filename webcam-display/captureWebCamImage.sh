#!/bin/bash

DATE=$(date +"%Y-%m-%d_%H%M")

fswebcam -r 1280x720 --no-banner /home/pi/cameraImages/$DATE.jpg
fswebcam -r 1280x720 --no-banner /home/pi/project-latex/webcam-display/webcam.jpg

# In order to run this automatically as a cron job, add the following line to crontab:
# * * * * * /home/pi/project-latex/webcam-display/captureWebCamImage.sh
# In order to edit crontab, run the following command:
# sudo crontab -e
