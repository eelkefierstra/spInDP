from ax12 import Ax12
from time import sleep
import sys

servo=Ax12()
stand=int(sys.argv[2])
id=int(sys.argv[1])

servo.move(id,stand)
