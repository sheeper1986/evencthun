package orderBookUpdated52_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class ManageOrders
{
	private Order order;
	private ArrayList<Order> orderList;
	
	public ManageOrders()
	{
		this(new Order());
		this.orderList = new ArrayList<Order>();
	}
	
	public ManageOrders(Order order) 
	{
		this.order = order;
	}

	
	public ArrayList<Order> cancelOrders(ArrayList<Order> aimedList, double price, double spread)
	{
		int i = 0;
		while(i < aimedList.size())
		{
			if(aimedList.get(i).isLimitOrder() && aimedList.get(i).isNewOrder() && Math.abs((aimedList.get(i).getPrice() - price)) > spread)
			{
				orderList.add(aimedList.get(i));
			}
				i++;
		}
		return orderList;
	}
	
	public void updatePendingOrderList(ArrayList<Order> aimedList)
	{
		if(order.isFilled() || order.isPartiallyFilled())
		{
			if(order.isMarketOrder())
			{
				int i = 0;
				while(i < aimedList.size())
				{
					if(order.getOrderID().equals(aimedList.get(i).getOrderID()))
					{
						aimedList.get(i).setDealingPrice(order.getDealingPrice());
						aimedList.get(i).setProcessedVolume(order.getProcessedVolume());
						aimedList.get(i).setStatus(order.getStatus());
					}
					i++;
				}
			}
			else//this.isLimitOrder
			{
				int i = 0;
				while(i < aimedList.size())
				{
					if(order.getOrderID().equals(aimedList.get(i).getOrderID()))
					{
						aimedList.get(i).setProcessedVolume(aimedList.get(i).getProcessedVolume() + order.getProcessedVolume());
						aimedList.get(i).setDealingPrice(order.getDealingPrice());
						aimedList.get(i).setStatus(order.getStatus());
					}
					i++;
				}
			}
		}
		else if(order.getStatus() == 3 || order.getStatus() == 4)
		{
			ListIterator<Order> it = aimedList.listIterator();				
			while(it.hasNext())
			{
				if(it.next().getOrderID().equals(order.getOrderID()))
				{
					it.remove();
				}
			}
		}		
	}
	
	public void updateLocalOrderbook(LinkedList<Order> buySideOrders, LinkedList<Order> sellSideOrders)
	{
		if(order.getOrderID().contains("Test"))
		{
			if(order.isBuySide())
			{
				if(order.isFilled())
				{
					int i = 0;
					while(i < buySideOrders.size())
					{
						if(order.getOrderID().equals(buySideOrders.get(i).getOrderID()))
						{
							buySideOrders.remove(i);
						}
						i++;
					}
				}
			}
			else//SellSide
			{
				if(order.isFilled())
				{
					int i = 0;
					while(i < sellSideOrders.size())
					{
						if(order.getOrderID().equals(sellSideOrders.get(i).getOrderID()))
						{
							sellSideOrders.remove(i);
						}
						i++;
					}
				}
			}
		}
		else//not Test
		{
			if(order.isBuySide())
			{
				if(order.isLimitOrder())
				{
					if(order.isNewOrder())
					{
						buySideOrders.add(order);
						Collections.sort(buySideOrders);	
					}
					else if(order.isFilled()||order.isCanceled())
					{
						int i = 0;
						while(i < buySideOrders.size())
						{
							if(order.getOrderID().equals(buySideOrders.get(i).getOrderID()))
							{
								buySideOrders.remove(i);
							}
							i++;
						}
					}
					else if(order.isPartiallyFilled())
					{
						int i = 0;
						while(i < buySideOrders.size())
						{
							if(order.getOrderID().equals(buySideOrders.get(i).getOrderID()))
							{
								buySideOrders.get(i).setVolume(buySideOrders.get(i).getVolume() - order.getProcessedVolume());
							}
							i++;
						}
					}
				}
			}
			else//SellSide
			{
				if(order.isLimitOrder())
				{
					if(order.isNewOrder())
					{
						sellSideOrders.add(order);
					    Collections.sort(sellSideOrders);
					}
					else if(order.isFilled()||order.isCanceled())
					{
						int i = 0;
						while(i < sellSideOrders.size())
						{
							if(order.getOrderID().equals(sellSideOrders.get(i).getOrderID()))
							{
								sellSideOrders.remove(i);
							}
							i++;
						}
					}
					else if(order.isPartiallyFilled())
					{
						int i = 0;
						while(i < sellSideOrders.size())
						{
							if(order.getOrderID().equals(sellSideOrders.get(i).getOrderID()))
							{
								sellSideOrders.get(i).setVolume(sellSideOrders.get(i).getVolume() - order.getProcessedVolume());
							}
							i++;
						}
					}
				}
			}
		}
		
	}
	
	public ArrayList<Order> recyclingOrders(ArrayList<Order> aimList)
	{
		int i = 0;
		while(i < aimList.size())
		{
			if(aimList.get(i).isNewOrder())
			{
				orderList.add(aimList.get(i));
			}
			i++;
		}
		return orderList;
	}
	
	public void cancelFrom(PriorityQueue<Order> aimedQueue)
	{
		Iterator<Order> it = aimedQueue.iterator();
		
		while(it.hasNext())
		{
			if(it.next().getOrderID().equals(order.getOrderID()))
			{
				it.remove();
			}
		}
	}
}
