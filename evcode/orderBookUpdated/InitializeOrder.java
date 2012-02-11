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
	
	public InitializeOrder()
	{
		this.rg = new RandomGenerator();
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
	
	public Order initNoiseOrder(double bestBidPrice, double bestAskPrice, int PercentOfMarket, int PercentOfBuy, String orderID)
	{
		Order order = new Order();	
		order.setOrderID(orderID);
		order.setSymbol("GOOGLE");		
		order.setOrderType(rg.randomType(PercentOfMarket));	
		
		if(order.isMarketOrder())
		{
			order.setSide(rg.randomSide(PercentOfBuy));
		}
		else//is limit
		{
			order.setSide(rg.randomSide(PercentOfBuy));
			
			if(order.isBuySide())
			{
				order.setPrice(rg.randomBidPrice(bestAskPrice));
			}
			else//SellSide
			{
				order.setPrice(rg.randomAskPrice(bestBidPrice));
			}	
		}
		order.setVolume(rg.randomVolume(1, 200));
		order.setOpenTime(System.currentTimeMillis());
		
		return order;
	}
	
	public Order createVWAPSellOrder(int totalVolume, long frequency, long timeLeft, String orderID, double VWAP, int PercentOfMarket, double percentage)
	{
		int vwapVolume = new VWAPVolume().getPendingVolume(totalVolume, frequency, timeLeft);
		
		Order order = new Order();	
		order.setOrderID(orderID);
		order.setSymbol("GOOGLE");
		order.setSide(SELL);
		order.setOrderType(rg.randomType(PercentOfMarket));
		if(order.isLimitOrder())
		{
			double myPrice =  new BigDecimal(VWAP*percentage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			order.setPrice(myPrice);
		}
		order.setVolume(vwapVolume);
		order.setOpenTime(System.currentTimeMillis());
		return order;
	}
	
	public Order createVWAPMarketSell(int totalVolume, String orderID)
	{
		Order order = new Order();	
		order.setOrderID(orderID);
		order.setSymbol("GOOGLE");
		order.setSide(SELL);
		order.setOrderType(MARKET);
		order.setVolume(totalVolume);
		order.setOpenTime(System.currentTimeMillis());
		return order;		
	}
}

