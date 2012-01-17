package orderBookUpdated29_1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class UpdateInventory {
	private Order order;
	private ArrayList<Order> pOList = new ArrayList<Order>();
	private ArrayList<Asset> assetList = new ArrayList<Asset>();
	private PriorityQueue<Order> oBList = new PriorityQueue<Order>();
	
	public UpdateInventory()
	{
		
	}
	
	public UpdateInventory(Order order, ArrayList<Order> pOList)
	{
		this.order = order;
		this.pOList = pOList; 
	}
	
	public UpdateInventory(Order order, PriorityQueue<Order> oBList)
	{
		this.order = order;
		this.oBList = oBList; 
	}
	
	public void updatePendingOrderList(Order order, ArrayList<Order> pOList)
	{
		if(pOList != null)
		{
			if(order.getStatus() == 1 || order.getStatus() == 2)
			{
				if(order.getType() == 1)
				{
					int i = 0;
					while(i < pOList.size())
					{
						if(pOList.get(i).getOrderID().equals(order.getOrderID()))
						{
							pOList.get(i).setDealingPrice(order.getDealingPrice());
							pOList.get(i).setProcessedVolume(order.getVolume());
							pOList.get(i).setStatus(order.getStatus());
						}
						i++;
					}
				}
				else if(order.getType() == 2)
				{
					int i = 0;
					while(i < pOList.size())
					{
						if(pOList.get(i).getOrderID().equals(order.getOrderID()))
						{
							int updatedVolume = pOList.get(i).getProcessedVolume() + order.getVolume();
							pOList.get(i).setProcessedVolume(updatedVolume);
							pOList.get(i).setStatus(order.getStatus());
						}
						i++;
					}
				}
			}
			else if(order.getStatus() == 3 || order.getStatus() == 4)
			{
				ListIterator<Order> it = pOList.listIterator();				
				while(it.hasNext())
				{
					if(it.next().getOrderID().equals(order.getOrderID()))
					{
						it.remove();
					}
				}
			}		
		}
	}
	
	public void updateQueue(Order order, PriorityQueue<Order> oBList)
	{
		Iterator<Order> it = oBList.iterator();
		
		while(it.hasNext())
		{
			if(it.next().getOrderID().equals(order.getOrderID()))
			{
				it.remove();
			}
		}
	}
	
	public ArrayList<Order> matchedOrderSpread(ArrayList<Order> pOList, double price, double spread)
	{
		ArrayList<Order> temp = new ArrayList<Order>();
		int i = 0;
		while(i < pOList.size())
		{
			if(pOList.get(i).getType() == 2 && pOList.get(i).getStatus() == 0 && Math.abs((pOList.get(i).getPrice() - price)) > spread)
			{
					temp.add(pOList.get(i));
			}
				i++;
		}
		return temp;
	}
}
