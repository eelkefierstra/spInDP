from ax12 import Ax12
from time import sleep

servo = Ax12();
stand=0
id=2
while(1):
	while(id<3):
		print(str(servo.move(id,stand)))
		print('moved '+str(id))
		sleep(0.5)
		id=id+1
	id=2
	if(stand==0):
		stand=512
	else:
		stand=0
