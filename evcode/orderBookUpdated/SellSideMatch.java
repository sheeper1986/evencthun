package orderBookUpdated20;

import java.util.PriorityQueue;

public class SellSideMatch 
{
	private PriorityQueue<Order> sellSideOrder = new PriorityQueue<Order>();
	private PriorityQueue<Order> buySideOrder = new PriorityQueue<Order>();
	private PriorityQueue<Order> processedOrder = new PriorityQueue<Order>();
	
	public SellSideMatch()
	{
		
	}
	
	public SellSideMatch(PriorityQueue<Order> sellSideOrder, PriorityQueue<Order> buySideOrder)
	{
		this.sellSideOrder = sellSideOrder;
		this.buySideOrder = buySideOrder;
	}
	
	public void setSQ(PriorityQueue<Order> sellSideOrder)
	{
		this.sellSideOrder = sellSideOrder;
	}
	public PriorityQueue<Order> getSQ()
	{
		return sellSideOrder;
	}
	
	public void setBQ(PriorityQueue<Order> buySideOrder)
	{
		this.buySideOrder = buySideOrder;
	}
	
	public PriorityQueue<Order> getBQ()
	{
		return buySideOrder;
	}
	
	public PriorityQueue<Order> matchOrder()
	{
		if(sellSideOrder.peek().isMarketOrder()&&buySideOrder.peek() == null)
		{
			 sellSideOrder.peek().setStatus(3);
			 processedOrder.add(sellSideOrder.poll());
			 
			 return processedOrder; 
		}
		else if (sellSideOrder.peek().isMarketOrder()&&buySideOrder.peek().isLimitOrder())
		{
			if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
			{
				sellSideOrder.peek().setStatus(1);
				buySideOrder.peek().setStatus(1);
				
                sellSideOrder.peek().setDealingPrice(buySideOrder.peek().getPrice());
				
				processedOrder.add(sellSideOrder.poll());
				processedOrder.add(buySideOrder.poll());
				
				return processedOrder;
			}
			else if(sellSideOrder.peek().getVolume() < buySideOrder.peek().getVolume())
			{
				int aVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
				
				int filledVolume = sellSideOrder.peek().getVolume();
				Order filledOrder = new Order();
				filledOrder.setAll(buySideOrder.peek());
				filledOrder.setStatus(2);
				filledOrder.setVolume(filledVolume);
				processedOrder.add(filledOrder);
				
				buySideOrder.peek().setVolume(aVolume);
				 
				sellSideOrder.peek().setStatus(1);
				sellSideOrder.peek().setDealingPrice(buySideOrder.peek().getPrice());
				processedOrder.add(sellSideOrder.poll());
				
				return processedOrder;
			}
			else
			{
				double sumSellPrice = 0;
			    int sumSellVolume = 0;
			    
				while(sellSideOrder.peek().getVolume() > buySideOrder.peek().getVolume())
				{
					int bVolume = sellSideOrder.peek().getVolume() - buySideOrder.peek().getVolume();
					sellSideOrder.peek().setVolume(bVolume);
					sumSellPrice += (buySideOrder.peek().getVolume())*(buySideOrder.peek().getPrice());
					sumSellVolume += buySideOrder.peek().getVolume();
					
					buySideOrder.peek().setStatus(1);
					processedOrder.add(buySideOrder.poll());
					
					if(buySideOrder.peek() == null)
					{
						sellSideOrder.peek().setStatus(2);
						sellSideOrder.peek().setVolume(sumSellVolume);
						sellSideOrder.peek().setDealingPrice(sumSellPrice/sumSellVolume);
						processedOrder.add(sellSideOrder.poll());
						
						break;
					}
					else
					{
						if(sellSideOrder.peek().getVolume() < buySideOrder.peek().getVolume())
						{
							int cVolume = buySideOrder.peek().getVolume() - (sellSideOrder.peek().getVolume());
							
							int filledVolume = sellSideOrder.peek().getVolume();
							Order filledOrder = new Order();
							filledOrder.setAll(buySideOrder.peek());
							filledOrder.setStatus(2);
							filledOrder.setVolume(filledVolume);
				            processedOrder.add(filledOrder);
							
							buySideOrder.peek().setVolume(cVolume);
							
							double allPrice = sumSellPrice+ (buySideOrder.peek().getPrice())*(sellSideOrder.peek().getVolume());
							int allVolume = sumSellVolume + (sellSideOrder.peek().getVolume());
							double averagePrice = (allPrice)/(allVolume);
							
							sellSideOrder.peek().setStatus(1);
							sellSideOrder.peek().setDealingPrice(averagePrice);
							sellSideOrder.peek().setVolume(allVolume);
							processedOrder.add(sellSideOrder.poll());
							
							break;
						}
						else if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
						{
							double allPrice = sumSellPrice+ (buySideOrder.peek().getPrice())*(sellSideOrder.peek().getVolume());
							int allVolume = sumSellVolume + (sellSideOrder.peek().getVolume());
							double averagePrice = (allPrice)/(allVolume);
							
							buySideOrder.peek().setStatus(1);
							sellSideOrder.peek().setStatus(1);
							sellSideOrder.peek().setDealingPrice(averagePrice);
							sellSideOrder.peek().setVolume(allVolume);

							processedOrder.add(sellSideOrder.poll());
							processedOrder.add(buySideOrder.poll());
							
							break;
						}
						else
							continue;
					}
				}
				return processedOrder;
			}
		}
		else if(((sellSideOrder.peek()!=null) &&sellSideOrder.peek().isLimitOrder())&&((buySideOrder.peek()!=null) &&buySideOrder.peek().isLimitOrder()))
		{
			if(sellSideOrder.peek().getPrice() == buySideOrder.peek().getPrice())
			{
				if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
				{
					 sellSideOrder.peek().setStatus(1);
					 buySideOrder.peek().setStatus(1);
						
					 processedOrder.add(sellSideOrder.poll());
					 processedOrder.add(buySideOrder.poll());
					 
					 return processedOrder;
				}
				else if(sellSideOrder.peek().getVolume() < buySideOrder.peek().getVolume())
				{
					 int aVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
					 
					 int filledVolume = sellSideOrder.peek().getVolume();
					 Order filledOrder = new Order();
					 filledOrder.setAll(buySideOrder.peek());
				     filledOrder.setStatus(2);
					 filledOrder.setVolume(filledVolume);
					 processedOrder.add(filledOrder);
					 
					 buySideOrder.peek().setVolume(aVolume);
					 
					 sellSideOrder.peek().setStatus(1);
					 processedOrder.add(sellSideOrder.poll());
					
					 return processedOrder;
				}
				else
				{
				    int sumSellVolume = 0;
				    
				    while(sellSideOrder.peek().getVolume() > buySideOrder.peek().getVolume())
				    {
				    	 int bVolume = sellSideOrder.peek().getVolume() - buySideOrder.peek().getVolume();
					     sellSideOrder.peek().setVolume(bVolume);
								
						 sumSellVolume += buySideOrder.peek().getVolume();
								
					     buySideOrder.peek().setStatus(1);
						 processedOrder.add(buySideOrder.poll());
						
						if(buySideOrder.peek() == null)
						{
							break;
						}
						else
						{

							if(sellSideOrder.peek().getPrice() != buySideOrder.peek().getPrice())
							{
								break;
							}
							else
							{
								if(sellSideOrder.peek().getVolume() < buySideOrder.peek().getVolume())
								{
									 int cVolume = buySideOrder.peek().getVolume() - (sellSideOrder.peek().getVolume());
									 
									 int filledVolume = sellSideOrder.peek().getVolume();
									 Order filledOrder = new Order();
									 filledOrder.setAll(buySideOrder.peek());
									 filledOrder.setStatus(2);
									 filledOrder.setVolume(filledVolume);
									 processedOrder.add(filledOrder);
									 
									 buySideOrder.peek().setVolume(cVolume);
									 
									 int allVolume = sumSellVolume + (sellSideOrder.peek().getVolume());
									 
									 sellSideOrder.peek().setStatus(1);
									 sellSideOrder.peek().setVolume(allVolume);
									 processedOrder.add(sellSideOrder.poll());
		
									 break;
								}	
									 else if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
									{
										 int allVolume = sumSellVolume + (sellSideOrder.peek().getVolume());
										 
										 sellSideOrder.peek().setStatus(1);
										 buySideOrder.peek().setStatus(1);
										 
										 sellSideOrder.peek().setVolume(allVolume);
										 
										 processedOrder.add(sellSideOrder.poll());
										 processedOrder.add(buySideOrder.poll());
										 
										 break;
									}
									 else
										 continue;
								}
							}
						}
						return processedOrder;
				    }
				}
				else
				 {
					 return processedOrder;
				 }
			 }
			else
			 {
				 return processedOrder;
			 }
		}
	}
				