from ax12 import Ax12
from time import sleep
import sys

servo=Ax12()
id=sys.argv[1]
i = 0
while i < 254:
	servo.factoryReset(i, True)
	i = i + 1
sleep(1)
servo.setID(1,int(id))
