from ax12 import Ax12
import sys

servo=Ax12()
id=sys.argv[1]

servo.setID(1,int(id))
