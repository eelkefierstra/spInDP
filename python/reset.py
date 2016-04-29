from ax12 import Ax12
servo = Ax12();

i=0
while(i<254):
    servo.factoryReset(i, True)
    print('reset servo' + str(i))
    i=i+1
