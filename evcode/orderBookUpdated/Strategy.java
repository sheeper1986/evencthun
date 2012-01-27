package orderBookUpdated50_6;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class Strategy 
{
	private Order order;
	private int trigger;
	private ArrayList<Order> pOList = new ArrayList<Order>();
	private ArrayList<Asset> assetList = new ArrayList<Asset>();
	private PriorityQueue<Order> oBBList = new PriorityQueue<Order>();
	private PriorityQueue<Order> oBSList = new PriorityQueue<Order>();
	
	public Strategy()
	{
		
	}
	
	/*public Strategy(PriorityQueue<Order> oBBList, PriorityQueue<Order> oBSList, int trigger)
	{
		this.oBBList = oBBList;
		this.oBSList = oBSList;
		this.trigger = trigger;
	}
	
	public void setListAndTrigger(PriorityQueue<Order> oBBList, PriorityQueue<Order> oBSList, int trigger)
	{
		this.oBBList = oBBList;
		this.oBSList = oBSList;
		this.trigger = trigger;
	}
	
	public PriorityQueue<Order> getOBBList()
	{
		return oBBList;
	}
	
	public PriorityQueue<Order> getOBSList()
	{
		return oBSList;
	}
	
	public int getTrigger()
	{
		return trigger;
	}*/

	
	public ArrayList<Order> matchedOrderSpread(ArrayList<Order> pOList, double price, double spread)
	{
		ArrayList<Order> temp = new ArrayList<Order>();
		int i = 0;
		while(i < pOList.size())
		{
			if(pOList.get(i).getOrderType() == 2 && pOList.get(i).getStatus() == 0 && Math.abs((pOList.get(i).getPrice() - price)) > spread)
			{
					temp.add(pOList.get(i));
			}
				i++;
		}
		return temp;
	}
}
