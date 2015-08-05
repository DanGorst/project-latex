project-latex [![Travis Results](https://travis-ci.org/DanGorst/project-latex.svg?branch=master)](https://travis-ci.org/DanGorst/project-latex)  
=============

Project to launch a high altitude balloon. This repo will contain code for both the on-board system and the ground station.

Configuration
-------------

To get the software running on the Pi at startup, add the following to the `/etc/rc.local` file:

```
# Auto run the balloon software
(cd /home/pi/project-latex; sudo ./runOnPi.sh &)
```
