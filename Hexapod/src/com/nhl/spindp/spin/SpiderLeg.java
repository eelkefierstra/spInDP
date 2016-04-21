package com.nhl.spindp.spin;

class SpiderLeg implements Runnable
{
	
	
	SpiderJoint[] servos = new SpiderJoint[3];
	
	public SpiderLeg()
	{
		for (byte i = 0; i <= SpiderJoint.MAX; i++)
		{
			servos[i] = new SpiderJoint();
		}
	}
	
	@Override
	public void run()
	{
		
		
	}
	
	private class SpiderJoint
	{
		private static final int COXA = 0;
		private static final int FEMUR = 1;
		private static final int TIBIA = 2;
		private static final int MAX   = TIBIA;
		
		private SpiderJoint()
		{
			
		}
	}

}
