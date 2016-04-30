from ax12 import Ax12
from time import sleep
self = Ax12();

print(self.readPosition(1))
print(self.readVoltage(1))
print(self.readTemperature(1));
