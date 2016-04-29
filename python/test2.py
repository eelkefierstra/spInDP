from ax12 import Ax12
from time import sleep
self = Ax12();
x=200
y=1
while(1):
    while(y<=3):
        self.move(y,x)
        sleep(0.33)
        y=y+1
    y=1
    if(x==0):
        x=500
    else:
        x=200


    


