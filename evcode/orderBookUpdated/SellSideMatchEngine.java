package orderBookUpdated52_5;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class SellSideMatchEngine
{
	private PriorityQueue<Order> sellSideOrders;
	private PriorityQueue<Order> buySideOrders;
	private ArrayList<Order> processedOrders = new ArrayList<Order>();
	private int id = 0;
	
	public SellSideMatchEngine()
	{
		this(new PriorityQueue<Order>(),new PriorityQueue<Order>());
	}
	
	public SellSideMatchEngine(PriorityQueue<Order> sellSideOrders, PriorityQueue<Order> buySideOrders)
	{
		this.sellSideOrders = sellSideOrders;
		this.buySideOrders = buySideOrders;
	}
	
	public void setSellSideOrders(PriorityQueue<Order> sellSideOrders)
	{
		this.sellSideOrders = sellSideOrders;
	}
	
	public void setBuySideOrders(PriorityQueue<Order> buySideOrders)
	{
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
	
	public ArrayList<Order> matchSellOrders()
	{
		if(sellSideOrders.peek().isMarketOrder())
		{
			if(buySideOrders.peek() != null)//buy limit
			{
				if(sellSideOrders.peek().getVolume() > buySideOrders.peek().getVolume())
				{
					double cumulatedPrice = 0;
				    int cumulatedVolume = 0;
				   
					while(sellSideOrders.peek().getVolume() > buySideOrders.peek().getVolume())
					{
						int leftVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
						sellSideOrders.peek().setVolume(leftVolume);
						cumulatedPrice += buySideOrders.peek().getVolume()*buySideOrders.peek().getPrice();
						cumulatedVolume += buySideOrders.peek().getVolume();
						
						buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
						buySideOrders.peek().setVolume(0);
						processedOrders.add(buySideOrders.poll());
						
						if(buySideOrders.peek() == null)
						{
							sellSideOrders.peek().setProcessedOrder(2, cumulatedVolume, new BigDecimal(cumulatedPrice/cumulatedVolume).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(), System.currentTimeMillis());
							sellSideOrders.peek().setVolume(0);
							processedOrders.add(sellSideOrders.poll());					
							break;
						}
						else
						{
							if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
							{
								int leftVolumeI = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();							
								int filledVolume = sellSideOrders.peek().getVolume();
								double totalPrice = cumulatedPrice + buySideOrders.peek().getPrice()*sellSideOrders.peek().getVolume();
								int totalVolume = cumulatedVolume + sellSideOrders.peek().getVolume();
							
								sellSideOrders.peek().setProcessedOrder(1, totalVolume, new BigDecimal(totalPrice/totalVolume).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(), System.currentTimeMillis());
								sellSideOrders.peek().setVolume(0);
								processedOrders.add(sellSideOrders.poll());			
								
								Order filledOrder = new Order();
								filledOrder.setAll(buySideOrders.peek());
								filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
						        processedOrders.add(filledOrder);
									
								buySideOrders.peek().setVolume(leftVolumeI);			
																
								break;
							}
							else if(sellSideOrders.peek().getVolume() == buySideOrders.peek().getVolume())
							{
								double totalPrice = cumulatedPrice + buySideOrders.peek().getPrice()*sellSideOrders.peek().getVolume();
								int totalVolume = cumulatedVolume + sellSideOrders.peek().getVolume();
								
								sellSideOrders.peek().setProcessedOrder(1, totalVolume, new BigDecimal(totalPrice/totalVolume).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue(), System.currentTimeMillis());
								sellSideOrders.peek().setVolume(0);
								processedOrders.add(sellSideOrders.poll());
								
								buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
								buySideOrders.peek().setVolume(0);
								processedOrders.add(buySideOrders.poll());		
												
								break;
							}
							else
								continue;
						}
					}
					return processedOrders;
				}
				else if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
				{
					int leftVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
					int filledVolume = sellSideOrders.peek().getVolume();
					
					sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
					sellSideOrders.peek().setVolume(0);
					processedOrders.add(sellSideOrders.poll());
					
					Order filledOrder = new Order();
					filledOrder.setAll(buySideOrders.peek());
					filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
					processedOrders.add(filledOrder);
						
					buySideOrders.peek().setVolume(leftVolume);
					
					return processedOrders;
				} 
				else
				{
					sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
					sellSideOrders.peek().setVolume(0);
					processedOrders.add(sellSideOrders.poll());
					
					buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
					buySideOrders.peek().setVolume(0);
					processedOrders.add(buySideOrders.poll());
									
					return processedOrders;
				}
			}
			else//buySide == null
			{
				sellSideOrders.peek().setProcessedOrder(3, System.currentTimeMillis());
				processedOrders.add(sellSideOrders.poll());
				
				return processedOrders; 
			}
		}
		else//SellSide is limit
		{
			if(buySideOrders.peek() != null)//BuySide is limit
			{
				if(sellSideOrders.peek().getPrice() == buySideOrders.peek().getPrice())
				{
					if(sellSideOrders.peek().getVolume() > buySideOrders.peek().getVolume())
					{   
					    while(sellSideOrders.peek().getVolume() > buySideOrders.peek().getVolume())
					    {
					    	int leftVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
					    	int filledVolume = buySideOrders.peek().getVolume();
					    	
					    	Order filledSellOrder = new Order();
							filledSellOrder.setAll(sellSideOrders.peek());
							filledSellOrder.setProcessedOrder(2, filledVolume, filledSellOrder.getPrice(), System.currentTimeMillis());
							processedOrders.add(filledSellOrder);
								
							sellSideOrders.peek().setVolume(leftVolume);
					    	
					    	buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
					    	buySideOrders.peek().setVolume(0);
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
								else//price equal
								{
									if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
									{	 
										 int leftVolumeI = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
										 int filledVolumeI = sellSideOrders.peek().getVolume();
										 
										 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
										 sellSideOrders.peek().setVolume(0);
										 processedOrders.add(sellSideOrders.poll());
										 
										 Order filledBuyOrder = new Order();
										 filledBuyOrder.setAll(buySideOrders.peek());
										 filledBuyOrder.setProcessedOrder(2, filledVolumeI, filledBuyOrder.getPrice(), System.currentTimeMillis());
										 processedOrders.add(filledBuyOrder);
											 
										 buySideOrders.peek().setVolume(leftVolumeI);	
										 
										 break;
									  }	
									  else if(sellSideOrders.peek().getVolume() == buySideOrders.peek().getVolume())
									  {
										  sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
										  sellSideOrders.peek().setVolume(0);
										  processedOrders.add(sellSideOrders.poll());
										  
										  buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis()); 
										  buySideOrders.peek().setVolume(0);
										  processedOrders.add(buySideOrders.poll());	
											 						 
										  break;
									  }
									  else
									  {
										  continue; 
									  }
								}
							}
						}
						return processedOrders;
					}
					else if(sellSideOrders.peek().getVolume() < buySideOrders.peek().getVolume())
					{
						int leftVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume(); 
					    int filledVolume = sellSideOrders.peek().getVolume();
						 
						sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
					    sellSideOrders.peek().setVolume(0);
						processedOrders.add(sellSideOrders.poll());
						 
						Order filledOrder = new Order();
						filledOrder.setAll(buySideOrders.peek());
						filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
						processedOrders.add(filledOrder);
							 
						buySideOrders.peek().setVolume(leftVolume);
						 
						return processedOrders;
					}
					else
					{
						sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
						sellSideOrders.peek().setVolume(0);
						processedOrders.add(sellSideOrders.poll());
						
						buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
						buySideOrders.peek().setVolume(0);
						processedOrders.add(buySideOrders.poll());
						
						return processedOrders;
					}
				}
				else
				{
					return processedOrders;
				}
			}
			else//buySide == null
			{
				return processedOrders;
			}
		}
	}
}
				