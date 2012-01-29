package orderBookUpdated50_9;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class LimitOrderBook
{
	private PriorityQueue<Order> buySideOrders = new PriorityQueue<Order>();
	private PriorityQueue<Order> sellSideOrders = new PriorityQueue<Order>();
	private ArrayList<Order> processedOrders = new ArrayList<Order>();
	
	public LimitOrderBook()
	{
		
	}
	
	public LimitOrderBook(PriorityQueue<Order> buySideOrders, PriorityQueue<Order> sellSideOrders)
	{
		this.buySideOrders = buySideOrders;
		this.sellSideOrders = sellSideOrders;
	}
	
	public void setBuySideOrderbook(PriorityQueue<Order> buySideOrders)
	{
		this.buySideOrders = buySideOrders;
	}
	
	public void setSellSideOrderbook(PriorityQueue<Order> sellSideOrders)
	{
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
	
	public ArrayList<Order> matchMechanism(PriorityQueue<Order> buySideOrders, PriorityQueue<Order> sellSideOrders)
	{
		if(buySideOrders.peek() != null)
		{
			if(buySideOrders.peek().isMarketOrder())//buy market
			{
				if(sellSideOrders.peek() != null)
				{
					if(sellSideOrders.peek().isLimitOrder())
					{
						if(buySideOrders.peek().getLeftVolume() == sellSideOrders.peek().getLeftVolume())
						{
							buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(buySideOrders.poll());
							
							sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(sellSideOrders.poll());
						}
						else if(buySideOrders.peek().getLeftVolume() < sellSideOrders.peek().getLeftVolume())
						{
							//int unfilledVolume = sellSideOrders.peek().getLeftVolume() - buySideOrders.peek().getLeftVolume();
							int filledVolume = buySideOrders.peek().getLeftVolume();
					
							buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(buySideOrders.poll());

							Order filledOrder = new Order();
							filledOrder.setAll(sellSideOrders.peek());
							filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
							processedOrders.add(filledOrder);
							sellSideOrders.peek().setProcessedVolume(filledVolume);
						}
						else
						{
							double totalBuyPrice = 0;
							int totalBuyVolume = 0;
							//int originalVolume = buySideOrders.peek().getVolume();
							
							while(buySideOrders.peek().getLeftVolume() > sellSideOrders.peek().getLeftVolume())
							{
								int filledVolume = sellSideOrders.peek().getLeftVolume();
								buySideOrders.peek().setProcessedVolume(filledVolume);
								totalBuyPrice += (sellSideOrders.peek().getLeftVolume())*(sellSideOrders.peek().getPrice());
								totalBuyVolume += sellSideOrders.peek().getLeftVolume();
								
								sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
								processedOrders.add(sellSideOrders.poll());
								
							
								if(sellSideOrders.peek() == null)
								{
									buySideOrders.peek().setProcessedOrder(2, totalBuyVolume, totalBuyPrice/totalBuyVolume, System.currentTimeMillis());
									//buySideOrders.peek().setVolume(originalVolume);
									processedOrders.add(buySideOrders.poll());					
									break;
								}
								else
								{
									if(buySideOrders.peek().getLeftVolume() < sellSideOrders.peek().getLeftVolume())
									{
										//int unfilledVolumeII = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();			
										int filledVolumeI = buySideOrders.peek().getLeftVolume();
										double totalPrice = totalBuyPrice + (sellSideOrders.peek().getPrice())*(buySideOrders.peek().getLeftVolume());
										int totalVolume = totalBuyVolume + (buySideOrders.peek().getLeftVolume());
										
										buySideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										//buySideOrders.peek().setProcessedVolume(filledVolumeI);
										processedOrders.add(buySideOrders.poll());
										
										
										Order filledOrder = new Order();
										filledOrder.setAll(sellSideOrders.peek());
										filledOrder.setProcessedOrder(2, filledVolumeI, filledOrder.getPrice(), System.currentTimeMillis());
										processedOrders.add(filledOrder);
											
										sellSideOrders.peek().setProcessedVolume(filledVolumeI);
										
										break;
									}
									else if(buySideOrders.peek().getLeftVolume() == sellSideOrders.peek().getLeftVolume())
									{
										double totalPrice = totalBuyPrice + (sellSideOrders.peek().getPrice())*(buySideOrders.peek().getLeftVolume());
										int totalVolume = totalBuyVolume + (buySideOrders.peek().getLeftVolume());
										
										buySideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										//buySideOrders.peek().setVolume(originalVolume);
										processedOrders.add(buySideOrders.poll());
										
										sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
								        processedOrders.add(sellSideOrders.poll());			
										
										break;
									}
									else
										continue;
								 }
							  }
						  }	
							
					}
					return processedOrders;
				}
				else//sell == null
				{
					buySideOrders.peek().setProcessedOrder(3, System.currentTimeMillis());
					processedOrders.add(buySideOrders.poll());
					return processedOrders;
				}
			}
			else// buy limit
			{
				if(sellSideOrders.peek() != null)
				{
					if(sellSideOrders.peek().isMarketOrder())
					{
						if(sellSideOrders.peek().getLeftVolume() == buySideOrders.peek().getLeftVolume())
						{
							sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(sellSideOrders.poll());
							
							buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(buySideOrders.poll());
						}
						else if(sellSideOrders.peek().getLeftVolume() < buySideOrders.peek().getLeftVolume())
						{
							//int unfilledVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
							int filledVolume = sellSideOrders.peek().getLeftVolume();
							
							sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(sellSideOrders.poll());

							Order filledOrder = new Order();
							filledOrder.setAll(buySideOrders.peek());
							filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
							processedOrders.add(filledOrder);
								
							buySideOrders.peek().setProcessedVolume(filledVolume);
						}
						else
						{
							double totalSellPrice = 0;
						    int totalSellVolume = 0;
						    //int originalVolume = sellSideOrders.peek().getVolume();
							while(sellSideOrders.peek().getLeftVolume() > buySideOrders.peek().getLeftVolume())
							{
								int filledVolume = buySideOrders.peek().getLeftVolume();
								sellSideOrders.peek().setProcessedVolume(filledVolume);
								totalSellPrice += (buySideOrders.peek().getLeftVolume())*(buySideOrders.peek().getPrice());
								totalSellVolume += buySideOrders.peek().getLeftVolume();
								

								buySideOrders.peek().setProcessedOrder(1, filledVolume, buySideOrders.peek().getPrice(), System.currentTimeMillis());
								processedOrders.add(buySideOrders.poll());
								
								if(buySideOrders.peek() == null)
								{
									sellSideOrders.peek().setProcessedOrder(2, totalSellVolume, totalSellPrice/totalSellVolume, System.currentTimeMillis());
									//sellSideOrders.peek().setVolume(originalVolume);
									processedOrders.add(sellSideOrders.poll());					
									break;
								}
								else
								{
									if(sellSideOrders.peek().getLeftVolume() < buySideOrders.peek().getLeftVolume())
									{
										//int unfilledVolumeII = buySideOrders.peek().getVolume() - (sellSideOrders.peek().getVolume());							
										int filledVolumeI = sellSideOrders.peek().getLeftVolume();
										double totalPrice = totalSellPrice + (buySideOrders.peek().getPrice())*(sellSideOrders.peek().getLeftVolume());
										int totalVolume = totalSellVolume + (sellSideOrders.peek().getLeftVolume());
									
										sellSideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										//sellSideOrders.peek().setVolume(originalVolume);
										processedOrders.add(sellSideOrders.poll());			

										Order filledOrder = new Order();
										filledOrder.setAll(buySideOrders.peek());
										filledOrder.setProcessedOrder(2, filledVolumeI, filledOrder.getPrice(), System.currentTimeMillis());
								        processedOrders.add(filledOrder);
											
										buySideOrders.peek().setProcessedVolume(filledVolumeI);			
										
											
										break;
									}
									else if(sellSideOrders.peek().getLeftVolume() == buySideOrders.peek().getLeftVolume())
									{
										double totalPrice = totalSellPrice + (buySideOrders.peek().getPrice())*(sellSideOrders.peek().getLeftVolume());
										int totalVolume = totalSellVolume + (sellSideOrders.peek().getLeftVolume());
										
										sellSideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										//sellSideOrders.peek().setVolume(originalVolume);
										processedOrders.add(sellSideOrders.poll());
										
										buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
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
					else//sell limit
					{
						 if(buySideOrders.peek().getPrice() == sellSideOrders.peek().getPrice())
						 {
							 if(buySideOrders.peek().getLeftVolume() == sellSideOrders.peek().getLeftVolume())
							 {
								 buySideOrders.peek().setProcessedOrder(1,  buySideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
								 processedOrders.add(buySideOrders.poll());
								
								 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
								 processedOrders.add(sellSideOrders.poll());
							 }
							 else if(buySideOrders.peek().getLeftVolume() < sellSideOrders.peek().getLeftVolume())
							 { 
								 //int unfilledVolume = sellSideOrders.peek().getVolume() - buySideOrders.peek().getVolume();
								 int filledVolume = buySideOrders.peek().getLeftVolume();
								 
								 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
								 processedOrders.add(buySideOrders.poll());
								 
								 Order filledOrder = new Order();
								 filledOrder.setAll(sellSideOrders.peek());
								 filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
								 processedOrders.add(filledOrder);

								 sellSideOrders.peek().setProcessedVolume(filledVolume);		
							 }
							 else
							 {				 
								 while(buySideOrders.peek().getLeftVolume() > sellSideOrders.peek().getLeftVolume())
								 {
									 //int unfilledBuySideVolume = buySideOrders.peek().getVolume() - sellSideOrders.peek().getVolume();
									 int filledBuySideVolume = sellSideOrders.peek().getLeftVolume();
									 
									 Order filledBuyOrder = new Order();
									 filledBuyOrder.setAll(buySideOrders.peek());
									 filledBuyOrder.setProcessedOrder(2, filledBuySideVolume, filledBuyOrder.getPrice(), System.currentTimeMillis());
									 processedOrders.add(filledBuyOrder);
										 
								     buySideOrders.peek().setProcessedVolume(filledBuySideVolume);				 
								   	 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
									 processedOrders.add(sellSideOrders.poll());

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
											 if(buySideOrders.peek().getLeftVolume() < sellSideOrders.peek().getLeftVolume())
											 {
												 //int unfilledSellSideVolume = sellSideOrders.peek().getVolume() - (buySideOrders.peek().getVolume());
												 int filledSellSideVolume = buySideOrders.peek().getLeftVolume();
												
												 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
												 processedOrders.add(buySideOrders.poll());		
												
												 Order filledSellOrder = new Order();
												 filledSellOrder.setAll(sellSideOrders.peek());
												 filledSellOrder.setProcessedOrder(2, filledSellSideVolume, filledSellOrder.getPrice(), System.currentTimeMillis());
												 processedOrders.add(filledSellOrder);
													 
												 sellSideOrders.peek().setProcessedVolume(filledSellSideVolume);		
												 	 
												 break;
											 }
											 else if(buySideOrders.peek().getLeftVolume() == sellSideOrders.peek().getLeftVolume())
											 {
												 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getLeftVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
												 processedOrders.add(buySideOrders.poll());
												
												 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getLeftVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
												 processedOrders.add(sellSideOrders.poll());								 
																		 
												 break;
											 }
											 else
												 continue;
										 }
									 } 
								}
							}
							 return processedOrders;
						}
						else
						{
							return processedOrders;
						}
					}
				}
				else//if(sellSideOrders.peek() == null)
				{
					return processedOrders;
				}
			}
		}
		else//buy == null
		{
			if(sellSideOrders.peek() != null)
			{
				if(sellSideOrders.peek().isMarketOrder())
				{
					sellSideOrders.peek().setProcessedOrder(3, System.currentTimeMillis());
					processedOrders.add(sellSideOrders.poll());
					return processedOrders;
				}
				else//sell is limit
				{
					return processedOrders;
				}
			}
			else// sell == null
			{
				return processedOrders;
			}
		}
	}
}

