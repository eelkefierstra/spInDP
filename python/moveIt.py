from ax12 import Ax12
from time import sleep

servo = Ax12();
speed=500
startStand=450
endStand=550
startID=2
endID=19

stand=startStand
id=startID
while(1):
	while(id<=endID):
		servo.move(id,stand)
		print('moved '+str(id))
		sleep(0.04)
		id=id+1
	id=startID
	if(stand==endStand):
		stand=startStand
	else:
		stand=endStand
