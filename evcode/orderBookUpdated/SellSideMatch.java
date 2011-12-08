package orderBookUpdated15;

import java.util.PriorityQueue;

public class SellSideMatch 
{
	private PriorityQueue<Order> sellSideOrder = new PriorityQueue<Order>();
	private PriorityQueue<Order> buySideOrder = new PriorityQueue<Order>();
	
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
	
	public void matchOrder()
	{
		if(sellSideOrder.peek().isMarketOrder()&&buySideOrder.peek() == null)
		{
			 System.out.println("NO LIQUIDITY " + sellSideOrder.poll() + " is Canceled");
			 System.out.println("---Updated--- " + sellSideOrder);
		}
		else if (sellSideOrder.peek().isMarketOrder()&&buySideOrder.peek().isLimitOrder())
		{
			if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
			{
				System.out.println("Volume " + sellSideOrder.peek().getVolume() + " deal with Price " + buySideOrder.peek().getPrice());
			
				System.out.println(sellSideOrder.poll() + " -----is removed");
				System.out.println(buySideOrder.poll() + " -----is removed");
			}
			else if(sellSideOrder.peek().getVolume()<buySideOrder.peek().getVolume())
			{
				int aVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
				buySideOrder.peek().setVolume(aVolume);
				System.out.println("Volume " + sellSideOrder.peek().getVolume() + " deal with Price " + buySideOrder.peek().getPrice());
			
				System.out.println(sellSideOrder.poll() + " -----is removed");
			}
			else
			{
				double sumSellPrice = 0;
			    int sumSellVolume = 0;
			    
				while(sellSideOrder.peek().getVolume()>buySideOrder.peek().getVolume())
				{
					int bVolume = sellSideOrder.peek().getVolume()-buySideOrder.peek().getVolume();
					sellSideOrder.peek().setVolume(bVolume);
					sumSellPrice += (buySideOrder.peek().getVolume())*(buySideOrder.peek().getPrice());
					sumSellVolume += buySideOrder.peek().getVolume();
			
					System.out.println(buySideOrder.poll() + " -----is removed");
					if(buySideOrder.peek() == null)
					{
						System.out.println("No Enough Liquidity, Partly Filled " + sumSellVolume + " with price " + (sumSellPrice/sumSellVolume));
						System.out.println("Rest of " + sellSideOrder.poll() + " -----is Canceled");
						break;
					}
					else
					{
						if(sellSideOrder.peek().getVolume()<=buySideOrder.peek().getVolume())
						{
							int cVolume = (buySideOrder.peek().getVolume()) - (sellSideOrder.peek().getVolume());
							System.out.println("-----removed volume " + sellSideOrder.peek().getVolume() + " from " + buySideOrder.peek());
							buySideOrder.peek().setVolume(cVolume);
							System.out.println("-----remain " + buySideOrder.peek());
							double allPrice = sumSellPrice+(buySideOrder.peek().getPrice())*(sellSideOrder.peek().getVolume());
							int allVolume = sumSellVolume+ (sellSideOrder.peek().getVolume());
							double averagePrice = (allPrice)/(allVolume);
							System.out.println("Volume " + allVolume + " deal with Price " + averagePrice);
							sellSideOrder.peek().setVolume(allVolume);
							System.out.println(sellSideOrder.poll() + " -----is removed");
							break;
						}
						else
							continue;
					}
				}
			}
		}
		else if(((sellSideOrder.peek()!=null) &&sellSideOrder.peek().isLimitOrder())&&((buySideOrder.peek()!=null) &&buySideOrder.peek().isLimitOrder()))
		{
			if(sellSideOrder.peek().getPrice() == buySideOrder.peek().getPrice())
			{
				if(sellSideOrder.peek().getVolume() == buySideOrder.peek().getVolume())
				{
					System.out.println("Volume " + sellSideOrder.peek().getVolume() + " deal with Price " + buySideOrder.peek().getPrice());
				    System.out.println(sellSideOrder.poll() + " -----is removed");
					System.out.println(buySideOrder.poll()+ " -----is removed");
				}
				else if(sellSideOrder.peek().getVolume() < buySideOrder.peek().getVolume())
				{
					int aVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
					buySideOrder.peek().setVolume(aVolume);
					System.out.println("Volume " + sellSideOrder.peek().getVolume() + " deal with Price " + buySideOrder.peek().getPrice());
				    System.out.println(sellSideOrder.poll() + " -----is removed"); 
				}
				else
				{
					double limitPrice = 0;
				    int sumSellVolume = 0;
				    
				    while(sellSideOrder.peek().getVolume() > buySideOrder.peek().getVolume())
				    {
				    	int bVolume = sellSideOrder.peek().getVolume() - buySideOrder.peek().getVolume();
						sellSideOrder.peek().setVolume(bVolume);
								
						limitPrice  = sellSideOrder.peek().getPrice();
						sumSellVolume += buySideOrder.peek().getVolume();
								
						System.out.println(buySideOrder.poll() + " -----is removed");
						System.out.println("Volume " + sumSellVolume + " has been processed, remaining " + sellSideOrder.peek());
						
						if(buySideOrder.peek()!=null)
						{
							if(sellSideOrder.peek().getPrice() != buySideOrder.peek().getPrice())
							{
								break;
							}
							if(sellSideOrder.peek().getVolume() <= buySideOrder.peek().getVolume())
							{
								int cVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
								System.out.println("-----removed volume " + sellSideOrder.peek().getVolume() + " from " + buySideOrder.peek());
								buySideOrder.peek().setVolume(cVolume);
								System.out.println("-----remain " + buySideOrder.peek());
								double allPrice = limitPrice;
								int allVolume = sumSellVolume + (sellSideOrder.peek().getVolume());
								System.out.println("Volume " + allVolume + " deal with Price " + limitPrice);
								sellSideOrder.peek().setVolume(allVolume);
								System.out.println(sellSideOrder.poll() + " -----is removed");
								
								if(buySideOrder.peek().getVolume() == 0)
								{
										buySideOrder.poll();
										
								}
								break;
							}
						}
						else
						{
							break;
						}
					}
				 }
			}
		}
	}
}
