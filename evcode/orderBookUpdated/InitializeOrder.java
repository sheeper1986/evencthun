package orderBookUpdated50_9;

import java.util.PriorityQueue;

public class InitializeOrder 
{
	private int id = 0;
	
	public InitializeOrder()
	{
		
	}

	public void initializeBuyOrder(PriorityQueue<Order> buySO, PriorityQueue<Order> sellSO, int orderQuantitiy)
	{
		for(int i = 0; i < orderQuantitiy; i++)
		{
			int randomVolume = (int)(10+Math.random()*200);
			int randomBuyPrice = (int)(40+Math.random()*10);
		
			Order newOrder = new Order();
			newOrder.setOrderType(2);
            newOrder.setOrderID("Test" + String.valueOf(id++));
			newOrder.setSymbol("GOOGLE");
			newOrder.setSide(1);
			newOrder.setVolume(randomVolume);
			newOrder.setPrice(randomBuyPrice);
			newOrder.setOpenTime(System.currentTimeMillis());
			
			buySO.add(newOrder);
		}
		for(int i = 0; i < orderQuantitiy; i++)
		{
			int randomVolume = (int)(10+Math.random()*200);
			int randomSellPrice = (int)(50+Math.random()*11);
		
			Order newOrder = new Order();
			newOrder.setOrderType(2);
            newOrder.setOrderID("Test" + String.valueOf(id++));
			newOrder.setSymbol("GOOGLE");
			newOrder.setSide(2);
			newOrder.setVolume(randomVolume);
			newOrder.setPrice(randomSellPrice);
			newOrder.setOpenTime(System.currentTimeMillis());
			
			sellSO.add(newOrder);
		}
	}
}
