package orderBookUpdated29_1;

import java.util.ArrayList;
import jade.content.AgentAction;

public class Order implements AgentAction,Comparable<Order>
{
	private int type, side, volume, processedVolume, status;
	private String symbol;
	private double price, dealingPrice;
	private Long openTime;
	private String orderID;
	
	//Constructor
	public Order()
	{
		
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
	
	public void setProcessedVolume(int processedVolume)
	{
		this.processedVolume = processedVolume;
	}
	public int getProcessedVolume()
	{
		return processedVolume;
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
	{	//
		//if(this.getStatus() == 0)
		//{
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
		//}
		//else
			//return this.getOpenTime() < order2.getOpenTime() ? -1 : (this.getOpenTime() > order2.getOpenTime() ? 1 : 0);
	}
	
	public String toString()
	{
		if(this.isNewOrder())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getProcessedVolume() + "/" + this.getVolume() + " " + OrderStatus.getOrderStatus(this));
			}
			else
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume "+ this.getProcessedVolume() + "/" + this.getVolume() + " Price " + this.getPrice() + " " + OrderStatus.getOrderStatus(this));
		}
		else if(this.isFilled()||this.isPartiallyFilled())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getProcessedVolume() + "/" + this.getVolume() + " with DealingPrice " + this.getDealingPrice() + " " + OrderStatus.getOrderStatus(this));
			}
			else
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume "+ this.getProcessedVolume() + "/" + this.getVolume()  + " with DealingPrice " + this.getPrice() + " " + OrderStatus.getOrderStatus(this));
		}
		else
		{
			return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume "+ this.getVolume() + " " + OrderStatus.getOrderStatus(this));
		}		
	}
	
	public Order setAll(Order order)
	{
		this.setOrderID(order.getOrderID());
		this.setSymbol(order.getSymbol());
		this.setType(order.getType());
		this.setSide(order.getSide());
		this.setVolume(order.getVolume());
		this.setProcessedVolume(order.getProcessedVolume());
		this.setPrice(order.getPrice());
		this.setDealingPrice(order.getDealingPrice());
		this.setOpenTime(order.getOpenTime());
		this.setStatus(order.getStatus());
		return this;
	}
	
	/*public Order sameOrderIn(ArrayList<Order> alo)
	{
		for(int i = 0; i < alo.size(); i++)
		{
			if(this.getOrderID().equals(alo.get(i).getOrderID()))
			{
				return alo.get(i);
			}
		}
		return null;
	}*/
}
