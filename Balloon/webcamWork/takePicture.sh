#!/bin/bash

DATE=$(date +"%Y-%m-%d_%H%M")

fswebcam -r 1280x720 --no-banner /home/pi/project-latex/webcamWork/$DATE.jpg

# In order to run this automatically as a cron job, add the following line to crontab:
# * * * * * /home/pi/project-latex/webcamWork/takePicture.sh
# In order to edit crontab, run the following command:
# sudo crontab -e
