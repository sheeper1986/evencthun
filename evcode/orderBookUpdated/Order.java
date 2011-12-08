package orderBookUpdated12;

import jade.content.AgentAction;

public class Order implements AgentAction,Comparable<Order>
{
	private int orderID, type, side, volume;
	private String symbol;
	private double price;
	private Long openTime;
	private static final double NO_PRICE = 0.0;
	private static final int NO_VOLUME = 0;
	
	//Constructor
	public Order()
	{
		
	}
	
	public Order(int orderID, String symbol, int volume)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
	}
	
	public Order(int orderID, String symbol, int volume, Long openTime )
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.openTime = openTime;
	}
	
	public Order(int orderID, String symbol, int volume, double price)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.price = price;
	}
	
	public Order(int orderID, String symbol, int volume, double price,  Long openTime)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.price = price;
		this.openTime = openTime;
	}
	
	//Set,Get orderID
	public void setOrderID(int orderID)
	{
		this.orderID = orderID;
	}
	public int getOrderID()
	{
		return orderID;
	}
	
	//Set,Get type
	public void setType(int type)
	{
		this.type = type;
	}
	public int getType()
	{
		return type;
	}
	
	//Set,Get side
	public void setSide(int side)
	{
		this.side = side;
	}
	public int getSide()
	{
		return side;
	}
	
	//Set,Get symbol
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}
	public String getSymbol()
	{
		return symbol;
	}
	
	//Set,Get volume
	public void setVolume(int volume)
	{
		this.volume = volume;
	}
	public int getVolume()
	{
		return volume;
	}
	
	//Set,Get price
	public void setPrice(double price)
	{
		this.price = price;
	}
	public double getPrice()
	{
		return price;
	}
	
	public void setOpenTime(Long openTime)
	{
		this.openTime = openTime;
	}
	
	public Long getOpenTime()
	{
		return openTime;
	}
	
	public boolean isLimitOrder()
	{
		return OrderType.LIMIT == OrderType.transType(this.getType());
	}
	
	public boolean isMarketOrder()
	{
		return OrderType.MARKET == OrderType.transType(this.getType());
	}
	
	public boolean isBuySide()
	{
		return OrderSide.BUY == OrderSide.transSide(this.getSide());
	}
	
	public boolean isSellSide()
	{
		return OrderSide.SELL == OrderSide.transSide(this.getSide());
	}

	
	public int compareTo(Order order2)
	{
		//
		if(this.isLimitOrder() && order2.isMarketOrder())
		{
			return 1;
		}
		
		if(this.isMarketOrder() && order2.isLimitOrder())
		{
			return -1;
		}
		
		if(this.isMarketOrder() && order2.isMarketOrder())
		{
			return this.getOpenTime() < order2.getOpenTime() ? -1 : (this.getOpenTime() > order2.getOpenTime() ? 1 : 0);
		}
		
		if(this.getPrice() < order2.getPrice())
		{
			return this.isBuySide() ? 1 : -1;
		}
		
		if(this.getPrice() > order2.getPrice())
		{
			return this.isBuySide() ? -1 : 1;
		}
		
		return this.getOpenTime() < order2.getOpenTime() ? -1 : (this.getOpenTime() > order2.getOpenTime() ? 1 : 0);
	}
	
	public String toString()
	{
		if(OrderType.getOrderType(this).equals(OrderType.MARKET))
		{
			return ("OrderID" + this.getOrderID() + " " + OrderSide.getSide(this).toString() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume"+ this.getVolume());
		}
		else if (OrderType.getOrderType(this).equals(OrderType.LIMIT))
			return ("OrderID" + this.getOrderID() + " " + OrderSide.getSide(this).toString() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume"+ this.getVolume() + " Price" + this.getPrice());
		else
			return "UNKNOWN";
	}
}
