package orderBookUpdated29_9;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class SellSideOrderMatch 
{
	private PriorityQueue<Order> sellSideOrders = new PriorityQueue<Order>();
	private PriorityQueue<Order> buySideOrders = new PriorityQueue<Order>();
	private ArrayList<Order> processedOrders = new ArrayList<Order>();
	
	public SellSideOrderMatch()
	{
		
	}
	
	public SellSideOrderMatch(PriorityQueue<Order> sellSideOrders, PriorityQueue<Order> buySideOrders)
	{
		this.sellSideOrders = sellSideOrders;
		this.buySideOrders = buySideOrders;
	}
	
	public void setOrderbook(PriorityQueue<Order> sellSideOrders, PriorityQueue<Order> buySideOrders)
	{
		this.sellSideOrders = sellSideOrders;
		this.buySideOrders = buySideOrders;
	}
	
	public PriorityQueue<Order> getSellSideOrderbook()
	{
		return sellSideOrders;
	}
	
	public PriorityQueue<Order> getBuySideOrderbook()
	{
		return buySideOrders;
	}
	
	public ArrayList<Order> matchSellOrder()
	{
		if(sellSideOrders.peek().isMarketOrder()&&buySideOrders.peek() == null)
		{
			sellSideOrders.peek().setProcessedOrder(3, System.currentTimeMillis());
			processedOrders.add(sellSideOrders.poll());
			
			return processedOrders; 
		}
		else if (sellSideOrders.peek().isMarketOrder()&&buySideOrders.peek().isLimitOrder())
		{
			if(sellSideOrders.peek().getVolume() == buySideOrders.peek().getVolume())
			{
				sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
				buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
                
				processedOrders.add(sellSideOrders.poll());
				processedOrders.add(buySideOrders.poll());
				
				return processedOrders;
			}
			else if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
			{
				int unfilledVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
				int filledVolume = sellSideOrders.peek().getVolume();
				
				sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
				processedOrders.add(sellSideOrders.poll());
				
				Order filledOrder = new Order();
				filledOrder.setAll(buySideOrders.peek());
				filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
				processedOrders.add(filledOrder);
				
				buySideOrders.peek().setVolume(unfilledVolume);
				
				return processedOrders;
			}
			else
			{
				double totalSellPrice = 0;
			    int totalSellVolume = 0;
			    int originalVolume = sellSideOrders.peek().getVolume();
				while(sellSideOrders.peek().getVolume() > buySideOrders.peek().getVolume())
				{
					int unfilledVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
					sellSideOrders.peek().setVolume(unfilledVolume);
					totalSellPrice += (buySideOrders.peek().getVolume())*(buySideOrders.peek().getPrice());
					totalSellVolume += buySideOrders.peek().getVolume();
					
					buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
					processedOrders.add(buySideOrders.poll());
					
					if(buySideOrders.peek() == null)
					{
						sellSideOrders.peek().setProcessedOrder(2, totalSellVolume, totalSellPrice/totalSellVolume, System.currentTimeMillis());
						sellSideOrders.peek().setVolume(originalVolume);
						processedOrders.add(sellSideOrders.poll());					
						break;
					}
					else
					{
						if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
						{
							int unfilledVolumeII = buySideOrders.peek().getVolume() - (sellSideOrders.peek().getVolume());							
							int filledVolume = sellSideOrders.peek().getVolume();
							double totalPrice = totalSellPrice + (buySideOrders.peek().getPrice())*(sellSideOrders.peek().getVolume());
							int totalVolume = totalSellVolume + (sellSideOrders.peek().getVolume());
							
							sellSideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
							sellSideOrders.peek().setVolume(originalVolume);
							processedOrders.add(sellSideOrders.poll());			
							
							Order filledOrder = new Order();
							filledOrder.setAll(buySideOrders.peek());
							filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
				            processedOrders.add(filledOrder);
							
							buySideOrders.peek().setVolume(unfilledVolumeII);				
							break;
						}
						else if(sellSideOrders.peek().getVolume() == buySideOrders.peek().getVolume())
						{
							double totalPrice = totalSellPrice + (buySideOrders.peek().getPrice())*(sellSideOrders.peek().getVolume());
							int totalVolume = totalSellVolume + (sellSideOrders.peek().getVolume());
							
							sellSideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
							sellSideOrders.peek().setVolume(originalVolume);
							buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());

							processedOrders.add(sellSideOrders.poll());
							processedOrders.add(buySideOrders.poll());							
							break;
						}
						else
							continue;
					}
				}
				return processedOrders;
			}
		}
		else if(((sellSideOrders.peek()!=null) &&sellSideOrders.peek().isLimitOrder())&&((buySideOrders.peek()!=null) &&buySideOrders.peek().isLimitOrder()))
		{
			if(sellSideOrders.peek().getPrice() == buySideOrders.peek().getPrice())
			{
				if(sellSideOrders.peek().getVolume() == buySideOrders.peek().getVolume())
				{
					 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
					 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
					 
					 processedOrders.add(sellSideOrders.poll());
					 processedOrders.add(buySideOrders.poll());
					 
					 return processedOrders;
				}
				else if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
				{
					 int unfilledVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume(); 
					 int filledVolume = sellSideOrders.peek().getVolume();
					 
					 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
					 processedOrders.add(sellSideOrders.poll());
			
					 Order filledOrder = new Order();
					 filledOrder.setAll(buySideOrders.peek());
					 filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
					 processedOrders.add(filledOrder);
					 
					 buySideOrders.peek().setVolume(unfilledVolume);

					 return processedOrders;
				}
				else
				{   
				    while(sellSideOrders.peek().getVolume() > buySideOrders.peek().getVolume())
				    {
				    	int unfilledSellSideVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
				    	int filledSellSideVolume = buySideOrders.peek().getVolume();
				    	
				    	Order filledSellOrder = new Order();
						filledSellOrder.setAll(sellSideOrders.peek());
						filledSellOrder.setProcessedOrder(2, filledSellSideVolume, filledSellOrder.getPrice(), System.currentTimeMillis());
						processedOrders.add(filledSellOrder);
						 
					     sellSideOrders.peek().setVolume(unfilledSellSideVolume);
								
					     buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
						 processedOrders.add(buySideOrders.poll());
						
						if(buySideOrders.peek() == null)
						{
							break;
						}
						else
						{

							if(sellSideOrders.peek().getPrice() != buySideOrders.peek().getPrice())
							{
								break;
							}
							else
							{
								if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
								{	 
									 int unfilledBuySideVolume = buySideOrders.peek().getVolume() - (sellSideOrders.peek().getVolume());
									 int filledBuySideVolume = sellSideOrders.peek().getVolume();
									 
									 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
									 processedOrders.add(sellSideOrders.poll());
									 
									 Order filledBuyOrder = new Order();
									 filledBuyOrder.setAll(buySideOrders.peek());
									 filledBuyOrder.setProcessedOrder(2, filledBuySideVolume, filledBuyOrder.getPrice(), System.currentTimeMillis());
									 processedOrders.add(filledBuyOrder);
									 
									 buySideOrders.peek().setVolume(unfilledBuySideVolume);	
									 break;
								}	
									 else if(sellSideOrders.peek().getVolume() == buySideOrders.peek().getVolume())
									{
										 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
										 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
										 
										 processedOrders.add(sellSideOrders.poll());
										 processedOrders.add(buySideOrders.poll());									 
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
				