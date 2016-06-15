from ax12 import Ax12
from time import sleep
import sys
self = Ax12();
for x in xrange(1,19):
	id = int(x)
	print('{}:{}').format(id, self.readPosition(id))
	#print(self.readVoltage(id))
	#print(self.readTemperature(id))
