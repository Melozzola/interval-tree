package riz.silvano.intervaltree;

import java.util.concurrent.atomic.AtomicBoolean;

import riz.silvano.intervaltree.IntervalTree.TreeStatusListener;

public class StatusListener implements TreeStatusListener
{
	private AtomicBoolean loaded = new AtomicBoolean(false);

	public boolean isLoaded() 
	{
		return loaded.get();
	}

	public void loaded() 
	{
		loaded.set(true);
	}
}
