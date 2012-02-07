package orderBookUpdated52_2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class InitializeOrder 
{
	private int id = 0;
	private final int BUY = 1;
	private final int SELL = 2;
	private final int MARKET = 1;
	private final int LIMIT = 2;
	private RandomGenerator rg;
	private ArrayList<Order> orderList;
	
	public InitializeOrder()
	{
		this.rg = new RandomGenerator();
		this.orderList = new ArrayList<Order>();
	}

	public void initOrderbook(PriorityQueue<Order> buySO, PriorityQueue<Order> sellSO, int orderQuantitiy)
	{
		for(int i = 0; i < orderQuantitiy; i++)
		{
			Order order = new Order();
            order.setOrderID("Test" + String.valueOf(id++));
			order.setSymbol("GOOGLE");
			order.setSide(BUY);
			order.setOrderType(LIMIT);
			order.setVolume(rg.randomVolume(1, 200));
			order.setPrice(rg.randomInitBuyPrice(50, 5));
			order.setOpenTime(System.currentTimeMillis());			
			buySO.add(order);
		}
		for(int i = 0; i < orderQuantitiy; i++)
		{	
			Order order = new Order();
            order.setOrderID("Test" + String.valueOf(id++));
			order.setSymbol("GOOGLE");
			order.setSide(SELL);
			order.setOrderType(LIMIT);
			order.setVolume(rg.randomVolume(1, 200));
			order.setPrice(rg.randomInitSellPrice(50, 5));
			order.setOpenTime(System.currentTimeMillis());			
			sellSO.add(order);
		}
	}
	
	public Order initNoiseOrder(double bestBidPrice, double BestAskPrice, String orderID)
	{
		Order order = new Order();	
		order.setOrderID(orderID);
		order.setSymbol("GOOGLE");		
		order.setOrderType(rg.randomType(40));	
		
		if(order.isMarketOrder())
		{
			order.setSide(rg.randomSide(50));
			order.setVolume(rg.randomVolume(1, 200));
			order.setOpenTime(System.currentTimeMillis());
		}
		else//is limit
		{
			order.setSide(rg.randomSide(50));
			
			if(order.isBuySide())
			{
				
				order.setPrice(rg.randomBidPrice(bestBidPrice));
			}
			else//SellSide
			{
				order.setPrice(rg.randomAskPrice(BestAskPrice));
			}	
			order.setVolume(rg.randomVolume(1, 200));
			order.setOpenTime(System.currentTimeMillis());
		}
		return order;
	}
	
	public ArrayList<Order> createVWAPOrders(int volumeForSale, int frequency, int timeSolt, String orderID)
	{
		ArrayList<Integer> volumeList = new VolumeCutter().getVolumeList(volumeForSale, frequency, timeSolt);
		for(int i = 0; i < volumeList.size(); i++)
		{
			Order order = new Order();	
			order.setOrderID(orderID);
			order.setSymbol("GOOGLE");
			order.setSide(SELL);
			order.setOrderType(rg.randomType(40));
			//if(order.isLimitOrder())
			//{
				//double myPrice =  new BigDecimal(VWAPPrice*1.002).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				//order.setPrice(myPrice);
			//}
			order.setVolume(volumeList.get(i));
			order.setOpenTime(System.currentTimeMillis());
			orderList.add(order);
		}
		return orderList;
	}
}
