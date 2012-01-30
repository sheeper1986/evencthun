package orderBookUpdated50_91;

import java.util.PriorityQueue;

public class InitializeOrder 
{
	private int id = 0;
	private RandomGenerator rg = new RandomGenerator();
	
	public InitializeOrder()
	{
		
	}

	public void initializeBuyOrder(PriorityQueue<Order> buySO, PriorityQueue<Order> sellSO, int orderQuantitiy)
	{
		for(int i = 0; i < orderQuantitiy; i++)
		{
			Order newOrder = new Order();
			newOrder.setOrderType(2);
            newOrder.setOrderID("Test" + String.valueOf(id++));
			newOrder.setSymbol("GOOGLE");
			newOrder.setSide(1);
			newOrder.setVolume(rg.randomVolume(10, 200));
			newOrder.setPrice(rg.randomInitBuyPrice(50, 5));
			newOrder.setOpenTime(System.currentTimeMillis());			
			buySO.add(newOrder);
		}
		for(int i = 0; i < orderQuantitiy; i++)
		{	
			Order newOrder = new Order();
			newOrder.setOrderType(2);
            newOrder.setOrderID("Test" + String.valueOf(id++));
			newOrder.setSymbol("GOOGLE");
			newOrder.setSide(2);
			newOrder.setVolume(rg.randomVolume(10, 200));
			newOrder.setPrice(rg.randomInitSellPrice(50, 5));
			newOrder.setOpenTime(System.currentTimeMillis());			
			sellSO.add(newOrder);
		}
	}
}
