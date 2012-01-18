package orderBookUpdated29_2;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class SellSideMatch 
{
	private PriorityQueue<Order> sellSideOrder = new PriorityQueue<Order>();
	private PriorityQueue<Order> buySideOrder = new PriorityQueue<Order>();
	private ArrayList<Order> processedOrder = new ArrayList<Order>();
	
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
	
	public ArrayList<Order> matchOrder()
	{
		if(sellSideOrder.peek().isMarketOrder()&&buySideOrder.peek() == null)
		{
			 sellSideOrder.peek().setStatus(3);
			 sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
			 processedOrder.add(sellSideOrder.poll());
			 
			 return processedOrder; 
		}
		else if (sellSideOrder.peek().isMarketOrder()&&buySideOrder.peek().isLimitOrder())
		{
			if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
			{
				sellSideOrder.peek().setStatus(1);
				sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
				sellSideOrder.peek().setDealingPrice(buySideOrder.peek().getPrice());
				
				buySideOrder.peek().setStatus(1);
				buySideOrder.peek().setOpenTime(System.currentTimeMillis());
                
				processedOrder.add(sellSideOrder.poll());
				processedOrder.add(buySideOrder.poll());
				
				return processedOrder;
			}
			else if(sellSideOrder.peek().getVolume() < buySideOrder.peek().getVolume())
			{
				int aVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
				int filledVolume = sellSideOrder.peek().getVolume();
				
				sellSideOrder.peek().setStatus(1);
				sellSideOrder.peek().setDealingPrice(buySideOrder.peek().getPrice());
				sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
				processedOrder.add(sellSideOrder.poll());
				
				Order filledOrder = new Order();
				filledOrder.setAll(buySideOrder.peek());
				filledOrder.setStatus(2);
				filledOrder.setVolume(filledVolume);
				filledOrder.setOpenTime(System.currentTimeMillis());
				processedOrder.add(filledOrder);
				
				buySideOrder.peek().setVolume(aVolume);
				
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
					buySideOrder.peek().setOpenTime(System.currentTimeMillis());
					processedOrder.add(buySideOrder.poll());
					
					if(buySideOrder.peek() == null)
					{
						sellSideOrder.peek().setStatus(2);
						sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
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
							double allPrice = sumSellPrice+ (buySideOrder.peek().getPrice())*(sellSideOrder.peek().getVolume());
							int allVolume = sumSellVolume + (sellSideOrder.peek().getVolume());
							double averagePrice = (allPrice)/(allVolume);
							
							sellSideOrder.peek().setStatus(1);
							sellSideOrder.peek().setDealingPrice(averagePrice);
							sellSideOrder.peek().setVolume(allVolume);
							processedOrder.add(sellSideOrder.poll());			
							
							Order filledOrder = new Order();
							filledOrder.setAll(buySideOrder.peek());
							filledOrder.setStatus(2);
							filledOrder.setVolume(filledVolume);
							filledOrder.setOpenTime(System.currentTimeMillis());
				            processedOrder.add(filledOrder);
							
							buySideOrder.peek().setVolume(cVolume);				
							break;
						}
						else if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
						{
							double allPrice = sumSellPrice+ (buySideOrder.peek().getPrice())*(sellSideOrder.peek().getVolume());
							int allVolume = sumSellVolume + (sellSideOrder.peek().getVolume());
							double averagePrice = (allPrice)/(allVolume);
							
							sellSideOrder.peek().setStatus(1);
							sellSideOrder.peek().setDealingPrice(averagePrice);
							sellSideOrder.peek().setVolume(allVolume);
							buySideOrder.peek().setStatus(1);
							buySideOrder.peek().setOpenTime(System.currentTimeMillis());

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
					 sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
					 buySideOrder.peek().setStatus(1);
					 buySideOrder.peek().setOpenTime(System.currentTimeMillis());
					 
					 processedOrder.add(sellSideOrder.poll());
					 processedOrder.add(buySideOrder.poll());
					 
					 return processedOrder;
				}
				else if(sellSideOrder.peek().getVolume() < buySideOrder.peek().getVolume())
				{
					 int aVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume(); 
					 int filledVolume = sellSideOrder.peek().getVolume();
					 
					 sellSideOrder.peek().setStatus(1);
					 sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
					 processedOrder.add(sellSideOrder.poll());
			
					 Order filledOrder = new Order();
					 filledOrder.setAll(buySideOrder.peek());
				     filledOrder.setStatus(2);
					 filledOrder.setVolume(filledVolume);
					 filledOrder.setOpenTime(System.currentTimeMillis());
					 processedOrder.add(filledOrder);
					 
					 buySideOrder.peek().setVolume(aVolume);

					 return processedOrder;
				}
				else
				{   
				    while(sellSideOrder.peek().getVolume() > buySideOrder.peek().getVolume())
				    {
				    	 int bVolume = sellSideOrder.peek().getVolume() - buySideOrder.peek().getVolume();
				    	 int filledSellVolume = buySideOrder.peek().getVolume();
				    	 
						 Order filledSellOrder = new Order();
						 filledSellOrder.setAll(sellSideOrder.peek());
					     filledSellOrder.setStatus(2);
						 filledSellOrder.setVolume(filledSellVolume);
						 filledSellOrder.setOpenTime(System.currentTimeMillis());
						 processedOrder.add(filledSellOrder);
						 
					     sellSideOrder.peek().setVolume(bVolume);
								
					     buySideOrder.peek().setStatus(1);
					     buySideOrder.peek().setOpenTime(System.currentTimeMillis());
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
									 int filledBuyVolume = sellSideOrder.peek().getVolume();
									 
									 sellSideOrder.peek().setStatus(1);
									 sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
									 processedOrder.add(sellSideOrder.poll());
									 
									 Order filledBuyOrder = new Order();
									 filledBuyOrder.setAll(buySideOrder.peek());
									 filledBuyOrder.setStatus(2);
									 filledBuyOrder.setVolume(filledBuyVolume);
									 filledBuyOrder.setOpenTime(System.currentTimeMillis());
									 processedOrder.add(filledBuyOrder);
									 
									 buySideOrder.peek().setVolume(cVolume);	
									 break;
								}	
									 else if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
									{
										 sellSideOrder.peek().setStatus(1);
										 sellSideOrder.peek().setOpenTime(System.currentTimeMillis());
										 buySideOrder.peek().setStatus(1);
										 buySideOrder.peek().setOpenTime(System.currentTimeMillis());
										 
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
				