package orderBookUpdated50_91;

import jade.core.Agent;

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
	private LinkedList<Order> oBBList = new LinkedList<Order>();
	private LinkedList<Order> oBSList = new LinkedList<Order>();
	//private InvestorAgent a;
	private int id = 0;
	
	public Strategy()
	{
		
	}
	
	//public Strategy(InvestorAgent a)
	//{
		//this.a = a;
	//}
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

	
	public ArrayList<Order> cancelOrders(ArrayList<Order> pOList, double price, double spread)
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
