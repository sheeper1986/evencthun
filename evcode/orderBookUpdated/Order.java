package orderBookUpdated52_5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import jade.content.AgentAction;

public class Order implements AgentAction,Comparable<Order>
{
	private int orderType, side, volume, processedVolume, status;
	private String symbol;
	private double price, dealingPrice;
	private Long openTime;
	private String orderID;
	private ArrayList<Order> pOList = new ArrayList<Order>();
	private PriorityQueue<Order> oBList = new PriorityQueue<Order>();
	
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
	public void setOrderType(int orderType)
	{
		this.orderType = orderType;
	}
	public int getOrderType()
	{
		return orderType;
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
	
	public void setProcessedOrder(int status, int processedVolume, double dealingPrice, Long openTime)
	{
		this.status = status;
		this.processedVolume = processedVolume;
		this.dealingPrice = dealingPrice;
		this.openTime = openTime;
	}
	
	public void setProcessedOrder(int status, Long openTime)
	{
		this.status = status;
		this.openTime = openTime;
	}
	
	public boolean isMarketOrder()
	{
		return this.getOrderType() == 1;
	}
	
	public boolean isLimitOrder()
	{
		return this.getOrderType() == 2;
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
	
	public int compareTo(Order newOrder)
	{
		if(this.isLimitOrder() && newOrder.isMarketOrder())
		{
			return 1;
		}
			
		if(this.isMarketOrder() && newOrder.isLimitOrder())
		{
			return -1;
		}
			
		if(this.isMarketOrder() && newOrder.isMarketOrder())
		{
			return this.getOpenTime() < newOrder.getOpenTime() ? -1 : (this.getOpenTime() > newOrder.getOpenTime() ? 1 : 0);
		}
			
		if(this.getPrice() < newOrder.getPrice())
		{
			return this.isBuySide() ? 1 : -1;
		}
			
		if(this.getPrice() > newOrder.getPrice())
		{
			return this.isBuySide() ? -1 : 1;
		}
			
		return this.getOpenTime() < newOrder.getOpenTime() ? -1 : (this.getOpenTime() > newOrder.getOpenTime() ? 1 : 0);
	}
	
	public Order setAll(Order order)
	{
		this.setOrderID(order.getOrderID());
		this.setSymbol(order.getSymbol());
		this.setOrderType(order.getOrderType());
		this.setSide(order.getSide());
		this.setVolume(order.getVolume());
		this.setProcessedVolume(order.getProcessedVolume());
		this.setPrice(order.getPrice());
		this.setDealingPrice(order.getDealingPrice());
		this.setOpenTime(order.getOpenTime());
		this.setStatus(order.getStatus());
		return this;
	}
	
	public String toString()
	{
		if(this.isNewOrder()||this.isCanceled())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getVolume() + " " + OrderStatus.getOrderStatus(this));
			}
			else
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getVolume() + " Price " + this.getPrice() + " " + OrderStatus.getOrderStatus(this));
		}
		else if(this.isFilled()||this.isPartiallyFilled())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getProcessedVolume() + "/" + this.getVolume() + " with DealingPrice " + this.getDealingPrice() + " " + OrderStatus.getOrderStatus(this));
			}
			else
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume "+ this.getProcessedVolume() + "/" + this.getVolume()  + " with DealingPrice " + this.getDealingPrice() + " " + OrderStatus.getOrderStatus(this));
		}
		else
		{
			return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getVolume() + " " + OrderStatus.getOrderStatus(this));
		}		
	}
}
