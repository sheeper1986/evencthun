package orderBookUpdated16;

import jade.content.AgentAction;

public class Order implements AgentAction,Comparable<Order>
{
	private int type, side, volume, status;
	private String symbol;
	private double price, dealingPrice;
	private Long openTime;
	private String orderID;
	
	//Constructor
	public Order()
	{
		
	}
	
	public Order(String orderID, String symbol, int volume)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
	}
	
	public Order(String orderID, String symbol, int volume, Long openTime )
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.openTime = openTime;
	}
	
	public Order(String orderID, String symbol, int volume, double price)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.price = price;
	}
	
	public Order(String orderID, String symbol, int volume, double price, Long openTime)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.price = price;
		this.openTime = openTime;
	}
	
	public Order(String orderID, String symbol, int volume, double price, Long openTime, int status)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.price = price;
		this.openTime = openTime;
		this.status = status;
	}
	
	public Order(String orderID, String symbol, int volume, double price, double dealingPrice, Long openTime, int status)
	{
		this.orderID = orderID;
		this.symbol = symbol;
		this.volume = volume;
		this.price = price;
		this.dealingPrice = dealingPrice;
		this.openTime = openTime;
		this.status = status;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	//Set,Get orderID
	public void setOrderID(String orderID)
	{
		this.orderID = orderID;
	}
	public String getOrderID()
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
	
	public void setDealingPrice(double dealingPrice)
	{
		this.dealingPrice = dealingPrice;
	}
	public double getDealingPrice()
	{
		return dealingPrice;
	}
	
	public void setOpenTime(Long openTime)
	{
		this.openTime = openTime;
	}
	
	public Long getOpenTime()
	{
		return openTime;
	}
	
	public boolean isMarketOrder()
	{
		return this.getType() == 1;
	}
	
	public boolean isLimitOrder()
	{
		return this.getType() == 2;
	}
	
	public boolean isBuySide()
	{
		return this.getSide() == 1;
	}
	
	public boolean isSellSide()
	{
		return this.getSide() == 2;
	}
	
	public boolean isCancelSide()
	{
		return this.getSide() == 3;
	}
	
	public boolean isNewOrder()
	{
		return this.getStatus() == 0;
	}
	
	public boolean isFilled()
	{
		return this.getStatus() == 1;
	}
	
	public boolean isPartiallyFilled()
	{
		return this.getStatus() == 2;
	}
	
	public boolean isRejected()
	{
		return this.getStatus() == 3;
	}
	
	public boolean isCanceled()
	{
		return this.getStatus() == 4;
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
		if(this.isNewOrder())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume"+ this.getVolume());
			}
			else
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume"+ this.getVolume() + " with Price" + this.getPrice());
		}
		else if(this.isFilled()||this.isPartiallyFilled())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " " + OrderStatus.getOrderStatus(this) + " Volume"+ this.getVolume() + " with DealingPrice " + this.getDealingPrice());
			}
			else
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " " + OrderStatus.getOrderStatus(this)  + " Volume"+ this.getVolume()  + " with DealingPrice " + this.getPrice());
		}
		else if(this.isRejected())
		{
			return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume"+ this.getVolume() + " " + OrderStatus.getOrderStatus(this));
		}
		else
			return ("OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume"+ this.getVolume() + " " + OrderStatus.getOrderStatus(this));
			
	}
	
	public Order setAll(Order order)
	{
		this.setOrderID(order.getOrderID());
		this.setSymbol(order.getSymbol());
		this.setType(order.getType());
		this.setSide(order.getSide());
		this.setVolume(order.getVolume());
		this.setPrice(order.getPrice());
		this.setDealingPrice(order.getDealingPrice());
		this.setOpenTime(order.getOpenTime());
		this.setStatus(order.getStatus());
		return this;
	}
}
