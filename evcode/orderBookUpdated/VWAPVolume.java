package orderBookUpdated52_5;

import java.util.LinkedList;

public class VWAPVolume
{
	LinkedList<Integer> list;
	
	public VWAPVolume()
	{
		this(new LinkedList<Integer>());
	}
	
	public VWAPVolume(LinkedList<Integer> list) 
	{
		this.list = list;
	}
	
	public int getPendingVolume(int totalVolume, long frequency, long timeLeft)
	{
		int rest = totalVolume%(int)(timeLeft/frequency);
		
		for(int i = 0; i < (int)timeLeft/frequency; i++)
		{
			list.add(totalVolume/(int)(timeLeft/frequency));
		}
		for(int i = 0; i < rest; i++)
		{
			list.set(i, (int)(list.get(i))+1);
		}		
		return list.peek();
	}
}
