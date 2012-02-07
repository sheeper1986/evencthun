package orderBookUpdated52_2;

import java.util.ArrayList;

public class VolumeCutter 
{
	ArrayList<Integer> list;
	
	public VolumeCutter()
	{
		this(new ArrayList<Integer>());
	}
	
	public VolumeCutter(ArrayList<Integer> list) 
	{
		this.list = list;
	}
	
	public ArrayList<Integer> getVolumeList(int number, int frequency, int timeSlot)
	{
		int rest = number%(timeSlot/frequency);
		
		for(int i = 0; i < timeSlot/frequency; i++)
		{
			list.add(number/(timeSlot/frequency));
		}
		for(int i = 0; i < rest; i++)
		{
			list.set(i, (Integer)(list.get(i))+1);
		}		
		return list;
	}
}
