from ax12 import Ax12
from time import sleep
self = Ax12();
x=0
y=1
while(1):
    while(y<=17):
        self.move(y,x)
        sleep(0.33)
        y=y+1
    y=1
    if(x==0):
        x=1000
    else:
        x=0


    


