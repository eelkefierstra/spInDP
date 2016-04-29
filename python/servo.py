from ax12 import Ax12
from time import sleep
self = Ax12();
servonummer=3

x=0
while(x<254):
    self.factoryReset(x,True)
    x=x+1
    print(x)
    sleep(0.05)

self.setID(1,servonummer)

#self.move(servonummer,1000)
