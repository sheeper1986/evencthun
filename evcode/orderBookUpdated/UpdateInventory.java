package orderBookUpdated16;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class UpdateInventory {
	private Order order;
	private ArrayList<Order> proposedOrder = new ArrayList<Order>();
	private PriorityQueue<Order> nonfilledOrder = new PriorityQueue<Order>();
	
	public UpdateInventory()
	{
		
	}
	
	public UpdateInventory(ArrayList<Order> proposedOrder, Order order)
	{
		this.order = order;
		this.proposedOrder = proposedOrder; 
	}
	
	public UpdateInventory(PriorityQueue<Order> nonfilledOrder, Order order)
	{
		this.order = order;
		this.nonfilledOrder = nonfilledOrder; 
	}
	
	public void updateList(ArrayList<Order> proposedOrder, Order order)
	{
		ListIterator<Order> it = proposedOrder.listIterator();
		
		while(it.hasNext())
		{
			if(it.next().getOrderID().equals(order.getOrderID()))
			{
				it.remove();
			}
		}
	}
	
	public void updateQueue(PriorityQueue<Order> nonfilledOrder, Order order)
	{
		Iterator<Order> it = nonfilledOrder.iterator();
		
		while(it.hasNext())
		{
			if(it.next().getOrderID().equals(order.getOrderID()))
			{
				it.remove();
			}
		}
	}
	
	/*public void updateListLimit(ArrayList<Order> proposedOrder, Order order)
	{
		ListIterator<Order> it = proposedOrder.listIterator();
		
		while(it.hasNext())
		{
			if(it.next().getOrderID().equals(order.getOrderID()))
			{
				int restVolume = it.next().getVolume() - order.getVolume();
				Order newOrder = new Order();
				newOrder.setAll(order);
				newOrder.setVolume(restVolume);
				it.set(newOrder);
			}
		}
	}*/
}
