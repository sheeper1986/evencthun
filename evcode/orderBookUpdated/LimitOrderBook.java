package orderBookUpdated50_7;

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
						if(buySideOrders.peek().getOriginalVolume() == sellSideOrders.peek().getOriginalVolume())
						{
							buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(buySideOrders.poll());
							
							sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(sellSideOrders.poll());
						}
						else if(buySideOrders.peek().getOriginalVolume() < sellSideOrders.peek().getOriginalVolume())
						{
							int unfilledVolume = sellSideOrders.peek().getOriginalVolume() - buySideOrders.peek().getOriginalVolume();
							int filledVolume = buySideOrders.peek().getOriginalVolume();
					
							buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(buySideOrders.poll());

							Order filledOrder = new Order();
							filledOrder.setAll(sellSideOrders.peek());
							filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
							processedOrders.add(filledOrder);
							sellSideOrders.peek().setOriginalVolume(unfilledVolume);
						}
						else
						{
							double totalBuyPrice = 0;
							int totalBuyVolume = 0;
							int originalVolume = buySideOrders.peek().getOriginalVolume();
							
							while(buySideOrders.peek().getOriginalVolume() > sellSideOrders.peek().getOriginalVolume())
							{
								int unfilledVolume = buySideOrders.peek().getOriginalVolume() - sellSideOrders.peek().getOriginalVolume();
								buySideOrders.peek().setOriginalVolume(unfilledVolume);
								totalBuyPrice += (sellSideOrders.peek().getOriginalVolume())*(sellSideOrders.peek().getPrice());
								totalBuyVolume += sellSideOrders.peek().getOriginalVolume();
								
								sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
								processedOrders.add(sellSideOrders.poll());
								
							
								if(sellSideOrders.peek() == null)
								{
									buySideOrders.peek().setProcessedOrder(2, totalBuyVolume, totalBuyPrice/totalBuyVolume, System.currentTimeMillis());
									buySideOrders.peek().setOriginalVolume(originalVolume);
									processedOrders.add(buySideOrders.poll());					
									break;
								}
								else
								{
									if(buySideOrders.peek().getOriginalVolume() < sellSideOrders.peek().getOriginalVolume())
									{
										int unfilledVolumeII = sellSideOrders.peek().getOriginalVolume() - buySideOrders.peek().getOriginalVolume();			
										int filledVolume = buySideOrders.peek().getOriginalVolume();
										double totalPrice = totalBuyPrice + (sellSideOrders.peek().getPrice())*(buySideOrders.peek().getOriginalVolume());
										int totalVolume = totalBuyVolume + (buySideOrders.peek().getOriginalVolume());
										
										buySideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										buySideOrders.peek().setOriginalVolume(originalVolume);
										processedOrders.add(buySideOrders.poll());
										
										
										Order filledOrder = new Order();
										filledOrder.setAll(sellSideOrders.peek());
										filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
										processedOrders.add(filledOrder);
											
										sellSideOrders.peek().setOriginalVolume(unfilledVolumeII);
										
										break;
									}
									else if(buySideOrders.peek().getOriginalVolume() == sellSideOrders.peek().getOriginalVolume())
									{
										double totalPrice = totalBuyPrice + (sellSideOrders.peek().getPrice())*(buySideOrders.peek().getOriginalVolume());
										int totalVolume = totalBuyVolume + (buySideOrders.peek().getOriginalVolume());
										
										buySideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										buySideOrders.peek().setOriginalVolume(originalVolume);
										processedOrders.add(buySideOrders.poll());
										
										sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
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
						if(sellSideOrders.peek().getOriginalVolume() == buySideOrders.peek().getOriginalVolume())
						{
							sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(sellSideOrders.poll());
							
							buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(buySideOrders.poll());
						}
						else if(sellSideOrders.peek().getOriginalVolume() < buySideOrders.peek().getOriginalVolume())
						{
							int unfilledVolume = buySideOrders.peek().getOriginalVolume() - sellSideOrders.peek().getOriginalVolume();
							int filledVolume = sellSideOrders.peek().getOriginalVolume();
							
							sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
							processedOrders.add(sellSideOrders.poll());

							Order filledOrder = new Order();
							filledOrder.setAll(buySideOrders.peek());
							filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
							processedOrders.add(filledOrder);
								
							buySideOrders.peek().setOriginalVolume(unfilledVolume);
						}
						else
						{
							double totalSellPrice = 0;
						    int totalSellVolume = 0;
						    int originalVolume = sellSideOrders.peek().getOriginalVolume();
							while(sellSideOrders.peek().getOriginalVolume() > buySideOrders.peek().getOriginalVolume())
							{
								int unfilledVolume = sellSideOrders.peek().getOriginalVolume() - buySideOrders.peek().getOriginalVolume();
								sellSideOrders.peek().setOriginalVolume(unfilledVolume);
								totalSellPrice += (buySideOrders.peek().getOriginalVolume())*(buySideOrders.peek().getPrice());
								totalSellVolume += buySideOrders.peek().getOriginalVolume();
								

								buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
								processedOrders.add(buySideOrders.poll());
								
								if(buySideOrders.peek() == null)
								{
									sellSideOrders.peek().setProcessedOrder(2, totalSellVolume, totalSellPrice/totalSellVolume, System.currentTimeMillis());
									sellSideOrders.peek().setOriginalVolume(originalVolume);
									processedOrders.add(sellSideOrders.poll());					
									break;
								}
								else
								{
									if(sellSideOrders.peek().getOriginalVolume() < buySideOrders.peek().getOriginalVolume())
									{
										int unfilledVolumeII = buySideOrders.peek().getOriginalVolume() - (sellSideOrders.peek().getOriginalVolume());							
										int filledVolume = sellSideOrders.peek().getOriginalVolume();
										double totalPrice = totalSellPrice + (buySideOrders.peek().getPrice())*(sellSideOrders.peek().getOriginalVolume());
										int totalVolume = totalSellVolume + (sellSideOrders.peek().getOriginalVolume());
									
										sellSideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										sellSideOrders.peek().setOriginalVolume(originalVolume);
										processedOrders.add(sellSideOrders.poll());			

										Order filledOrder = new Order();
										filledOrder.setAll(buySideOrders.peek());
										filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
								        processedOrders.add(filledOrder);
											
										buySideOrders.peek().setOriginalVolume(unfilledVolumeII);			
										
											
										break;
									}
									else if(sellSideOrders.peek().getOriginalVolume() == buySideOrders.peek().getOriginalVolume())
									{
										double totalPrice = totalSellPrice + (buySideOrders.peek().getPrice())*(sellSideOrders.peek().getOriginalVolume());
										int totalVolume = totalSellVolume + (sellSideOrders.peek().getOriginalVolume());
										
										sellSideOrders.peek().setProcessedOrder(1, totalVolume, totalPrice/totalVolume, System.currentTimeMillis());
										sellSideOrders.peek().setOriginalVolume(originalVolume);
										processedOrders.add(sellSideOrders.poll());
										
										buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
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
							 if(buySideOrders.peek().getOriginalVolume() == sellSideOrders.peek().getOriginalVolume())
							 {
								 buySideOrders.peek().setProcessedOrder(1,  buySideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
								 processedOrders.add(buySideOrders.poll());
								
								 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
								 processedOrders.add(sellSideOrders.poll());
							 }
							 else if(buySideOrders.peek().getOriginalVolume() < sellSideOrders.peek().getOriginalVolume())
							 { 
								 int unfilledVolume = sellSideOrders.peek().getOriginalVolume() - buySideOrders.peek().getOriginalVolume();
								 int filledVolume = buySideOrders.peek().getOriginalVolume();
								 
								 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
								 processedOrders.add(buySideOrders.poll());
								 
								 Order filledOrder = new Order();
								 filledOrder.setAll(sellSideOrders.peek());
								 filledOrder.setProcessedOrder(2, filledVolume, filledOrder.getPrice(), System.currentTimeMillis());
								 processedOrders.add(filledOrder);

								 sellSideOrders.peek().setOriginalVolume(unfilledVolume);		
							 }
							 else
							 {				 
								 while(buySideOrders.peek().getOriginalVolume() > sellSideOrders.peek().getOriginalVolume())
								 {
									 int unfilledBuySideVolume = buySideOrders.peek().getOriginalVolume() - sellSideOrders.peek().getOriginalVolume();
									 int filledBuySideVolume = sellSideOrders.peek().getOriginalVolume();
									 
									 Order filledBuyOrder = new Order();
									 filledBuyOrder.setAll(buySideOrders.peek());
									 filledBuyOrder.setProcessedOrder(2, filledBuySideVolume, filledBuyOrder.getPrice(), System.currentTimeMillis());
									 processedOrders.add(filledBuyOrder);
										 
								     buySideOrders.peek().setOriginalVolume(unfilledBuySideVolume);				 
								   	 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
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
											 if(buySideOrders.peek().getOriginalVolume() < sellSideOrders.peek().getOriginalVolume())
											 {
												 int unfilledSellSideVolume = sellSideOrders.peek().getOriginalVolume() - (buySideOrders.peek().getOriginalVolume());
												 int filledSellSideVolume = buySideOrders.peek().getOriginalVolume();
												
												 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
												 processedOrders.add(buySideOrders.poll());		
												
												 Order filledSellOrder = new Order();
												 filledSellOrder.setAll(sellSideOrders.peek());
												 filledSellOrder.setProcessedOrder(2, filledSellSideVolume, filledSellOrder.getPrice(), System.currentTimeMillis());
												 processedOrders.add(filledSellOrder);
													 
												 sellSideOrders.peek().setOriginalVolume(unfilledSellSideVolume);		
												 	 
												 break;
											 }
											 else if(buySideOrders.peek().getOriginalVolume() == sellSideOrders.peek().getOriginalVolume())
											 {
												 buySideOrders.peek().setProcessedOrder(1, buySideOrders.peek().getOriginalVolume(), buySideOrders.peek().getPrice(), System.currentTimeMillis());
												 processedOrders.add(buySideOrders.poll());
												
												 sellSideOrders.peek().setProcessedOrder(1, sellSideOrders.peek().getOriginalVolume(), sellSideOrders.peek().getPrice(), System.currentTimeMillis());
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

