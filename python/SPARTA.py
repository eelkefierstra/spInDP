from ax12 import Ax12
from time import sleep

servo = Ax12();
speed=500
stand=500
endID=18
id=1
while(id<=endID):
	servo.move(id,stand)
	print('moved '+str(id))
	sleep(0.5)
	id=id+1
