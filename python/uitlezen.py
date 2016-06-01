from ax12 import Ax12
from time import sleep
import sys
self = Ax12();
id = int(sys.argv[1])

print(self.readPosition(id))
print(self.readVoltage(id))
print(self.readTemperature(id))
