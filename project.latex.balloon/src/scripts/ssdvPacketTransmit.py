import serial
import time
import sys
import os

encoded_image_filepath = sys.argv[1]
packet_to_send = int(sys.argv[2])
baud_rate = int(sys.argv[3])
port = serial.Serial("/dev/ttyAMA0", baudrate = baud_rate, 
		stopbits = serial.STOPBITS_ONE, timeout = 0)

with open(encoded_image_filepath, "rb") as encodedImage:
    encodedImage.seek(packet_to_send*256)
    for i in range(0,16): 
        port.write(encodedImage.read(16))
	port.flush()
