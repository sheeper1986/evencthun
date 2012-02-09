package orderBookUpdated52_2;

import java.math.BigDecimal;
import java.util.ArrayList;

public class VWAP 
{
	private double marketVWAP;
	private double traderVWAP;
	private Long timeStamp;
	
	public VWAP()
	{

	}
	//public VWAP(ArrayList<Order> orderList)
	//{
		//this.orderList = orderList;
	//}
	
	public void setMarketPrice(double marketVWAP)
	{
		this.marketVWAP = marketVWAP;
	}
	
	public void setTraderPrice(double traderVWAP)
	{
		this.traderVWAP = traderVWAP;
	}
	
	public void setVWAPTime(Long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public double getMarketVWAP()
	{
		return marketVWAP;
	}
	
	public double getTraderVWAP()
	{
		return traderVWAP;
	}
	
	public Long getTime()
	{
		return timeStamp;
	}
	
	public void calculateVWAP(ArrayList<VWAP> vwapList, double marketVWAP, double traderVWAP)
	{
		VWAP v = new VWAP();
		v.setMarketPrice(marketVWAP);
		v.setTraderPrice(traderVWAP);
		v.setVWAPTime(System.currentTimeMillis());
		vwapList.add(v);
	}
	
	public String toString()
	{
		return this.getMarketVWAP() + " " + this.getTraderVWAP() + " " + this.getTime();
	}
}
