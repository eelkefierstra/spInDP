from ax12 import Ax12
from time import sleep

servo = Ax12();
speed=500
stand=512
stand2=650
stand3=50
endID=18
id=1
while(id<=endID):
	try:
		servo.move(id,stand)
		print('moved '+str(id))
	except:
		s=0
	sleep(0.4)
	try:
		servo.move(id+1,stand2)
	except:
		s=0
	sleep(0.4)
	print('moved '+str(id+1))
	try:
		servo.move(id+2,stand3)
	except:
		s=0
	print('moved '+str(id+2))
	sleep(0.04)
	id=id+3
