from ax12 import Ax12
from time import sleep
import sys

servo=Ax12()
oldId=sys.argv[1]
id=sys.argv[2]
servo.factoryReset(int(oldId), True)
sleep(1)
servo.setID(1,int(id))
