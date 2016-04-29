from ax12 import *
servo = Ax12();

i=0
while(i<254):
    servo.factoryReset(i, True)
    print('serve rest ' + str(i))
    i=i+1
