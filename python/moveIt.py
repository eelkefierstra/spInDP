from ax12 import Ax12
from time import sleep

servo = Ax12();
speed=500
stand=300
startID=1
endID=3
id=startID
while(1):
	while(id<=endID):
		servo.moveSpeed(id,stand,speed)
		print('moved '+str(id))
		sleep(0.5)
		id=id+1
	id=startID
	if(stand==600):
		stand=300
	else:
		stand=600
