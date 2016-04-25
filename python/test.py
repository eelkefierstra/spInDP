import serial
import time
import RPi.GPIO as GPIO
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(18,GPIO.OUT)
port = serial.Serial("/dev/ttyAMA0", baudrate=1000000, timeout=3.0)

while True:
    
    GPIO.output(18,GPIO.HIGH)
    port.write(bytearray.fromhex("FF FF FE 02 06 F7"))
    time.sleep(0.1)
    GPIO.output(18, GPIO.LOW)
    time.sleep(3)

    #GPIO.output(18,GPIO.HIGH)
    #port.write(bytearray.fromhex("FF FF 01 05 03 1E CD 00 0b"))
    #time.sleep(0.1)
    #GPIO.output(18, GPIO.LOW)
    #time.sleep(3)
