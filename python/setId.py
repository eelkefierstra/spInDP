from ax12 import Ax12
from time import sleep
import sys

servo=Ax12()
id=sys.argv[1]

servo.factoryReset(254, True)
sleep(1)
servo.setID(1,int(id))
