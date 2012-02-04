package orderBookUpdated52_1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class BuySideMatchEngine
{
	private PriorityQueue<Order> buySideOrders;
	private PriorityQueue<Order> sellSideOrders;
	private ArrayList<Order> processedOrders = new ArrayList<Order>();
	private int id = 0;
	
	public BuySideMatchEngine()
	{
		this(new PriorityQueue<Order>(),new PriorityQueue<Order>());
	}
	
	public BuySideMatchEngine(PriorityQueue<Order> buySideOrders, PriorityQueue<Order> sellSideOrders)
	{
		this.buySideOrders = buySideOrders;
		this.sellSideOrders = sellSideOrders;
	}
	
	public void setBuySideOrders(PriorityQueue<Order> buySideOrders)
	{
		this.buySideOrders = buySideOrders;
	}
	
	public void setSellSideOrders(PriorityQueue<Order> sellSideOrders)
	{
		this.sellSideOrders = sellSideOrders;
	}
	
	public PriorityQueue<Order> getBuySideOrders()
	{
		return buySideOrders;
	}
	
	public PriorityQueue<Order> getSellSideOrders()
	{
		return sellSideOrders;
	}
	
	public ArrayList<Order> matchBuyOrders()
	{
		if(buySideOrders.peek().isMarketOrder())
		{
			if(sellSideOrders.peek() != null)//SellSide is limit
			{
				if(buySideOrders.peek().getVolume() > sellSideOrders.peek().getVolume())
				{
					double cumulatedPrice = 0;
					int cumulatedVolume = 0;
					
					while(buySideOrders.peek().getVolume() > sellSideOrders.peek().getVolume())
					{
						int leftVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
						buySideOrders.peek().setVolume(leftVolume);
						cumulatedPrice += sellSideOrders.peek().getVolume()*sellSideOrders.peek().getPrice();
						cumulatedVolume += sellSideOrders.peek().getVolume();
						
						sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
						sellSideOrders.peek().setVolume(0);
						processedOrders.add(sellSideOrders.poll());						
					
						if(sellSideOrders.peek() == null)
						{
							buySideOrders.peek().setProcessedOrder(2, cumulatedVolume, new BigDecimal(cumulatedPrice/cumulatedVolume).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(), System.currentTimeMillis());
							buySideOrders.peek().setVolume(0);
							processedOrders.add(buySideOrders.poll());					
							break;
						}
						else
						{
							if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
							{
								int leftVolumeI = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();			
								int filledVolume = buySideOrders.peek().getVolume();
								double totalPrice = cumulatedPrice + sellSideOrders.peek().getPrice()*buySideOrders.peek().getVolume();
								int totalVolume = cumulatedVolume + buySideOrders.peek().getVolume();
								
								buySideOrders.peek().setProcessedOrder(1, totalVolume, new BigDecimal(totalPrice/totalVolume).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(), System.currentTimeMillis());
								buySideOrders.peek().setVolume(0);
								processedOrders.add(buySideOrders.poll());
								
								Order partlyFilledOrder = new Order();
								partlyFilledOrder.setAll(sellSideOrders.peek());
								partlyFilledOrder.setProcessedOrder(2, filledVolume, partlyFilledOrder.getPrice(), System.currentTimeMillis());
								processedOrders.add(partlyFilledOrder);
									
								sellSideOrders.peek().setVolume(leftVolumeI);
								
								break;
							}
							else if(buySideOrders.peek().getVolume() == sellSideOrders.peek().getVolume())
							{
								double totalPrice = cumulatedPrice + sellSideOrders.peek().getPrice()*buySideOrders.peek().getVolume();
								int totalVolume = cumulatedVolume + buySideOrders.peek().getVolume();
								
								buySideOrders.peek().setProcessedOrder(1, totalVolume, new BigDecimal(totalPrice/totalVolume).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(), System.currentTimeMillis());
								buySideOrders.peek().setVolume(0);
								processedOrders.add(buySideOrders.poll());
								
								sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
								sellSideOrders.peek().setVolume(0);
								processedOrders.add(sellSideOrders.poll());			
								
								break;
							}
							else
								continue;
						}
					}
					return processedOrders;
				}
				else if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
				{
					int leftVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
					int filledVolume = buySideOrders.peek().getVolume();
			
					buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
					buySideOrders.peek().setVolume(0);
					processedOrders.add(buySideOrders.poll());
					
					Order filledOrder = new Order();
					filledOrder.setAll(sellSideOrders.peek());
					filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
					processedOrders.add(filledOrder);
					
					sellSideOrders.peek().setVolume(leftVolume);
				
					return processedOrders;
				}
				else
				{
					buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
					buySideOrders.peek().setVolume(0);
					processedOrders.add(buySideOrders.poll());
					
					sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
					sellSideOrders.peek().setVolume(0);
					processedOrders.add(sellSideOrders.poll());
						
					return processedOrders;
				}
			}
			else//SellSide == null
			{
				buySideOrders.peek().setProcessedOrder(3, System.currentTimeMillis());
				processedOrders.add(buySideOrders.poll());
				
				return processedOrders;
			}
		}
		else//BuySide is limit
		{
			if(sellSideOrders.peek() != null)//SellSide is limit
			{
				if(buySideOrders.peek().getPrice() == sellSideOrders.peek().getPrice())
				{
					 if(buySideOrders.peek().getVolume() > sellSideOrders.peek().getVolume())
					 {				 
						 while(buySideOrders.peek().getVolume() > sellSideOrders.peek().getVolume())
						 {
							 int leftVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
							 int filledVolume = sellSideOrders.peek().getVolume();
							 
							 Order filledBuyOrder = new Order();
							 filledBuyOrder.setAll(buySideOrders.peek());
							 filledBuyOrder.setProcessedOrder(2, filledVolume, filledBuyOrder.getPrice(), System.currentTimeMillis());
							 processedOrders.add(filledBuyOrder);
								 
							 buySideOrders.peek().setVolume(leftVolume);
							 
						     sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
						     sellSideOrders.peek().setVolume(0);
							 processedOrders.add(sellSideOrders.poll());
						     
						     
							 if(sellSideOrders.peek() == null)
							 {
								 break;
							 }	 
							 else//SellSide != null
							 {
								 if(buySideOrders.peek().getPrice() != sellSideOrders.peek().getPrice())
								 {
									 break;
								 }
								 else
								 {
									 if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
									 {
										 int leftVolumeI = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
										 int filledVolumeI = buySideOrders.peek().getVolume();
										 
										 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
										 buySideOrders.peek().setVolume(0);
										 processedOrders.add(buySideOrders.poll());		
										 
										 Order filledSellOrder = new Order();
										 filledSellOrder.setAll(sellSideOrders.peek());
										 filledSellOrder.setProcessedOrder(2, filledVolumeI, filledSellOrder.getPrice(), System.currentTimeMillis());
										 processedOrders.add(filledSellOrder);
											 
										 sellSideOrders.peek().setVolume(leftVolumeI);		
										  
										 break;
									 }
									 else if(buySideOrders.peek().getVolume() == sellSideOrders.peek().getVolume())
									 {
										buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
										buySideOrders.peek().setVolume(0);
									    processedOrders.add(buySideOrders.poll());
										 
										sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
										sellSideOrders.peek().setVolume(0);
										processedOrders.add(sellSideOrders.poll());								 
										 						 
										 break;
									 }
									 else
										 continue;
								 }
							 } 					
						}
						return processedOrders;
					}///
					else if(buySideOrders.peek().getVolume() < sellSideOrders.peek().getVolume())
					{
						int leftVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
						int filledVolume = buySideOrders.peek().getVolume();
						 
						buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
						buySideOrders.peek().setVolume(0);
						processedOrders.add(buySideOrders.poll());
						 
						Order filledOrder = new Order();
						filledOrder.setAll(sellSideOrders.peek());
					    filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
						processedOrders.add(filledOrder);

						sellSideOrders.peek().setVolume(leftVolume);
						 
						return processedOrders;
					 }
					 else
					 {
						 buySideOrders.peek().setProcessedOrder(1,  buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
						 buySideOrders.peek().setVolume(0);
						 processedOrders.add(buySideOrders.poll());
						 
						 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
						 sellSideOrders.peek().setVolume(0);
						 processedOrders.add(sellSideOrders.poll());
						 
						 return processedOrders;
					 }
				}
				else//price not match
				{
					return processedOrders;
				}
			}
			else//SellSide == null
			{
				return processedOrders;
			}	
		}
	}
}

