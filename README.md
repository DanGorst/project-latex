project-latex [![Travis Results](https://travis-ci.org/DanGorst/project-latex.svg?branch=master)](https://travis-ci.org/DanGorst/project-latex)  
=============

Project to launch a high altitude balloon. This repo will contain code for both the on-board system and the ground station.


# Configuration
-------------

To build the software and copy it onto the Pi remotely from another computer on the same wifi network:

1. Make sure SSH is installed and enabled on both Pi and remote machine.
2. On the remote machine, run the buildToPi.sh script in the root folder of the repository.

  'sh buildToPi.sh IpAddressOfPi'

3. Connect to the Pi from the remote machine via ssh and run the runOnPi.sh script in /home/pi/project-latex

  'sh /home/pi/runOnPi.sh'

To get the software running on the Pi at startup, add the following to the `/etc/rc.local` file:

# Auto run the balloon software
--------------
'(cd /home/pi/project-latex; sudo ./runOnPi.sh &)'

