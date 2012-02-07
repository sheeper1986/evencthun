package orderBookUpdated52_2;

import java.util.ArrayList;

public class VWAP 
{
	private double vwapPrice;
	private Long vwapTime;
	private ArrayList<Order> orderList;
	
	public VWAP()
	{
		this(new ArrayList<Order>());
	}
	public VWAP(ArrayList<Order> orderList)
	{
		this.orderList = orderList;
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
	}
	
	public String toString()
	{
		return this.getVwapPrice() + " " + this.getVwapTime();
	}
}
