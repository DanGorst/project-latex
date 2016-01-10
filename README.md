project-latex [![Travis Results](https://travis-ci.org/DanGorst/project-latex.svg?branch=master)](https://travis-ci.org/DanGorst/project-latex)  
=============

Project to launch a high altitude balloon. This repo will contain code for both the on-board system and the ground station.


# Configuration
-------------

To build the software, copy it onto the Pi remotely from another machine on the same wifi network and run the software:

1. Make sure SSH is installed and enabled on both Pi and remote machine.
2. For the temperature sensor, make sure the the Pi has the following Python library installed:

  https://github.com/adafruit/Adafruit_Python_DHT

3. For the pressure sensor, make sure the Pi has i2c enabled, and the follwowing Python library installed:

  https://github.com/adafruit/Adafruit_Python_BMP

4. On the remote machine, run the buildToPi.sh script in the root folder of the repository.

  sh buildToPi.sh IpAddressOfPi

5. Connect to the Pi from the remote machine via ssh and run the runOnPi.sh script in /home/pi/project-latex

  sh /home/pi/runOnPi.sh


# Auto run the balloon software
--------------
To get the software running on the Pi at startup, add the following to the `/etc/rc.local` file:

(cd /home/pi/project-latex; sudo ./runOnPi.sh &)

