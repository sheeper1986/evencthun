package orderBookUpdated52_1;

import java.util.ArrayList;

public class VWAP 
{
	private double vwapPrice;
	private Long vwapTime;
	
	public VWAP()
	{

	}
	
	public VWAP(double vwapPrice, Long vwapTime)
	{
		this.vwapPrice = vwapPrice;
		this.vwapTime = vwapTime;
	}
	
	public void setVwapPrice(double vwapPrice)
	{
		this.vwapPrice = vwapPrice;
	}
	
	public void setVwapTime(Long vwapTime)
	{
		this.vwapTime = vwapTime;
	}

	public double getVwapPrice()
	{
		return vwapPrice;
	}
	
	public Long getVwapTime()
	{
		return vwapTime;
	}
	
	public void calculateVWAP(Order order, ArrayList<VWAP> vwapList, double totalPrice, int totalVolume)
	{
		VWAP v = new VWAP();
		v.setVwapPrice(totalPrice/totalVolume);
		v.setVwapTime(order.getOpenTime());
		vwapList.add(v);
		//return vwapList;
	}
	
	public String toString()
	{
		return this.getVwapPrice() + " " + this.getVwapTime();
	}
}
