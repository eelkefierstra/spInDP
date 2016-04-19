package com.nhl.spindp.spin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpiderBody
{
	ExecutorService executor;
	
	public SpiderBody()
	{
		executor = Executors.newFixedThreadPool(3);
	}
}
