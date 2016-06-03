from ax12 import Ax12

servo=Ax12()
i = 0
while i < 54:
	try:
		servo.ping(i)
		print("found on " + str(i) + "!!!!")
	except:
		s = 0
		#print("not on " + str(i))
		
	i = i + 1
