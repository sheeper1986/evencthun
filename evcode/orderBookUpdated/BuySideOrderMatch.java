package orderBookUpdated29_9_1;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class BuySideOrderMatch
{
	private PriorityQueue<Order> buySideOrders = new PriorityQueue<Order>();
	private PriorityQueue<Order> sellSideOrders = new PriorityQueue<Order>();
	private ArrayList<Order> processedOrders = new ArrayList<Order>();
	private int id = 0;
	
	public BuySideOrderMatch()
	{
		
	}
	
	public BuySideOrderMatch(PriorityQueue<Order> buySideOrders, PriorityQueue<Order> sellSideOrders)
	{
		this.buySideOrders = buySideOrders;
		this.sellSideOrders = sellSideOrders;
	}
	
	public void setOrderbook(PriorityQueue<Order> buySideOrders, PriorityQueue<Order> sellSideOrders)
	{
		this.buySideOrders = buySideOrders;
		this.sellSideOrders = sellSideOrders;
	}
	
	public PriorityQueue<Order> getBuySideOrderbook()
	{
		return buySideOrders;
	}
	
	public PriorityQueue<Order> getSellSideOrderbook()
	{
		return sellSideOrders;
	}
	
	public ArrayList<Order> matchBuyOrder()
	{
		
		 if(buySideOrders.peek().isMarketOrder()&&sellSideOrders.peek() == null)
		 {
			 buySideOrders.peek().setProcessedOrder(3, System.currentTimeMillis());
			 processedOrders.add(buySideOrders.poll());
			 
			 return processedOrders; 
		 }
		 else if (buySideOrders.peek().isMarketOrder()&&sellSideOrders.peek().isLimitOrder())
		 {
			if(buySideOrders.peek().getVolume() == sellSideOrders.peek().getVolume())
			{
				buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
				processedOrders.add(buySideOrders.poll());
				
				if(sellSideOrders.peek().getOrderID().contains("Test"))
				{
					sellSideOrders.poll();
				}
				else
				{
					sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
					processedOrders.add(sellSideOrders.poll());
				}
				
				return processedOrders;
			}
			else if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
			{
				int unfilledVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
				int filledVolume = buySideOrders.peek().getVolume();
		
				buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
				processedOrders.add(buySideOrders.poll());
				
				if(sellSideOrders.peek().getOrderID().contains("Test"))
				{
					sellSideOrders.peek().setVolume(unfilledVolume);
				}
				else
				{
					Order filledOrder = new Order();
					filledOrder.setAll(sellSideOrders.peek());
					filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
					processedOrders.add(filledOrder);
					sellSideOrders.peek().setVolume(unfilledVolume);
				}
				return processedOrders;
			}
			else
			{
				double totalBuyPrice = 0;
				int totalBuyVolume = 0;
				int originalVolume = buySideOrders.peek().getVolume();
				
				while(buySideOrders.peek().getVolume() > sellSideOrders.peek().getVolume())
				{
					int unfilledVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
					buySideOrders.peek().setVolume(unfilledVolume);
					totalBuyPrice += (sellSideOrders.peek().getVolume())*(sellSideOrders.peek().getPrice());
					totalBuyVolume += sellSideOrders.peek().getVolume();
					
					if(sellSideOrders.peek().getOrderID().contains("Test"))
					{
						sellSideOrders.poll();
					}
					else
					{
						sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
						processedOrders.add(sellSideOrders.poll());
					}
				
					if(sellSideOrders.peek() == null)
					{
						buySideOrders.peek().setProcessedOrder(2, totalBuyVolume, totalBuyPrice/totalBuyVolume, System.currentTimeMillis());
						buySideOrders.peek().setVolume(originalVolume);
						processedOrders.add(buySideOrders.poll());					
						break;
					}
					else
					{
						if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
						{
							int unfilledVolumeII = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();			
							int filledVolume = buySideOrders.peek().getVolume();
							double totalPrice = totalBuyPrice + (sellSideOrders.peek().getPrice())*(buySideOrders.peek().getVolume());
							int totalVolume = totalBuyVolume + (buySideOrders.peek().getVolume());
							
							buySideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
							buySideOrders.peek().setVolume(originalVolume);
							processedOrders.add(buySideOrders.poll());
							
							if(sellSideOrders.peek().getOrderID().contains("Test"))
							{
								sellSideOrders.peek().setVolume(unfilledVolumeII);
							}
							else
							{
								Order filledOrder = new Order();
								filledOrder.setAll(sellSideOrders.peek());
								filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
								processedOrders.add(filledOrder);
								
								sellSideOrders.peek().setVolume(unfilledVolumeII);
							}
							break;
						}
						else if(buySideOrders.peek().getVolume() == sellSideOrders.peek().getVolume())
						{
							double totalPrice = totalBuyPrice + (sellSideOrders.peek().getPrice())*(buySideOrders.peek().getVolume());
							int totalVolume = totalBuyVolume + (buySideOrders.peek().getVolume());
							
							buySideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
							buySideOrders.peek().setVolume(originalVolume);
							processedOrders.add(buySideOrders.poll());
							
							if(sellSideOrders.peek().getOrderID().contains("Test"))
							{
								sellSideOrders.poll();
							}
							else
							{
								sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
								processedOrders.add(sellSideOrders.poll());			
							}
							break;
						}
						else
							continue;
					 }
				  }
				return processedOrders;
			  }
		 }
		 else if(((buySideOrders.peek()!=null) &&buySideOrders.peek().isLimitOrder())&&((sellSideOrders.peek()!=null) &&sellSideOrders.peek().isLimitOrder()))
		 {
			 if(buySideOrders.peek().getPrice() == sellSideOrders.peek().getPrice())
			 {
				 if(buySideOrders.peek().getVolume() == sellSideOrders.peek().getVolume())
				 {
					 if(buySideOrders.peek().getOrderID().contains("Test"))
					 {
						 buySideOrders.poll();
					 }
					 else
					 {
						 buySideOrders.peek().setProcessedOrder(1,  buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
						 processedOrders.add(buySideOrders.poll());
					 }
					 if(sellSideOrders.peek().getOrderID().contains("Test"))
					 {
						 sellSideOrders.poll();
					 }
					 else
					 {
						 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
						 processedOrders.add(sellSideOrders.poll());
					 }
					 return processedOrders;
				 }
				 else if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
				 { 
					 int unfilledVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
					 int filledVolume = buySideOrders.peek().getVolume();
					 
					 if(buySideOrders.peek().getOrderID().contains("Test"))
					 {
						 buySideOrders.poll();
					 }
					 else
					 {
						 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
						 processedOrders.add(buySideOrders.poll());
					 }
					 if(sellSideOrders.peek().getOrderID().contains("Test"))
					 {
						 sellSideOrders.peek().setVolume(unfilledVolume);
					 }
					 else
					 {
						 Order filledOrder = new Order();
						 filledOrder.setAll(sellSideOrders.peek());
						 filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
						 processedOrders.add(filledOrder);

						 sellSideOrders.peek().setVolume(unfilledVolume);
					 }
					 return processedOrders;
				 }
				 else
				 {				 
					 while(buySideOrders.peek().getVolume() > sellSideOrders.peek().getVolume())
					 {
						 int unfilledBuySideVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
						 int filledBuySideVolume = sellSideOrders.peek().getVolume();
						 
						 if(buySideOrders.peek().getOrderID().contains("Test"))
						 {
							 buySideOrders.peek().setVolume(unfilledBuySideVolume);
						 }
						 else
						 {
							 Order filledBuyOrder = new Order();
							 filledBuyOrder.setAll(buySideOrders.peek());
							 filledBuyOrder.setProcessedOrder(2, filledBuySideVolume, filledBuyOrder.getPrice(), System.currentTimeMillis());
							 processedOrders.add(filledBuyOrder);
							 
						     buySideOrders.peek().setVolume(unfilledBuySideVolume);
						 }
					     if(sellSideOrders.peek().getOrderID().contains("Test"))
					     {
					    	 sellSideOrders.poll(); 
					     }
					     else
					     {
					    	 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
							 processedOrders.add(sellSideOrders.poll());
					     }
					     
						 if(sellSideOrders.peek() == null)
						 {
							 break;
						 }
							 
						 else
						 {
							 if(buySideOrders.peek().getPrice() != sellSideOrders.peek().getPrice())
							 {
								 break;
							 }
							 else
							 {
								 if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
								 {
									 int unfilledSellSideVolume = sellSideOrders.peek().getVolume() - (buySideOrders.peek().getVolume());
									 int filledSellSideVolume = buySideOrders.peek().getVolume();
									 
									 if(buySideOrders.peek().getOrderID().contains("Test"))
									 {
										 buySideOrders.poll();
									 }
									 else
									 {
										 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
										 processedOrders.add(buySideOrders.poll());		
									 }	
									 if(sellSideOrders.peek().getOrderID().contains("Test"))
									 {
										 sellSideOrders.peek().setVolume(unfilledSellSideVolume);	
									 }
									 else
									 {
										 Order filledSellOrder = new Order();
										 filledSellOrder.setAll(sellSideOrders.peek());
										 filledSellOrder.setProcessedOrder(2, filledSellSideVolume, filledSellOrder.getPrice(), System.currentTimeMillis());
										 processedOrders.add(filledSellOrder);
										 
										 sellSideOrders.peek().setVolume(unfilledSellSideVolume);		
									 }	 
									 break;
								 }
								 else if(buySideOrders.peek().getVolume() == sellSideOrders.peek().getVolume())
								 {
									 if(buySideOrders.peek().getOrderID().contains("Test"))
									 {
										 buySideOrders.poll();
									 }
									 else
									 {
										 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
										 processedOrders.add(buySideOrders.poll());
									 }
									 if(sellSideOrders.peek().getOrderID().contains("Test"))
									 {
										 sellSideOrders.poll(); 
									 }
									 else
									 {
										 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
										 processedOrders.add(sellSideOrders.poll());								 
									 }							 
									 break;
								 }
								 else
									 continue;
							 }
						 } 
					
					}
					return processedOrders;
				}
			}
			 else
			 {
				 return processedOrders;
			 }
		 }
		 else
		 {
			 return processedOrders;
		 }
	}
}

