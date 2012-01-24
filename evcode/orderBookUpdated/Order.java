package orderBookUpdated50;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;

import jade.content.AgentAction;

public class Order implements AgentAction,Comparable<Order>
{
	private int orderType, side, originalVolume, processedVolume, leftVolume, status;
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
	public void setOriginalVolume(int originalVolume)
	{
		this.originalVolume = originalVolume;
	}
	public int getOriginalVolume()
	{
		return originalVolume;
	}
	
	public void setProcessedVolume(int processedVolume)
	{
		this.processedVolume = processedVolume;
	}
	public int getProcessedVolume()
	{
		return processedVolume;
	}
	
	public void setLeftVolume(int leftVolume)
	{
		this.leftVolume = leftVolume;
	}
	public int getLeftVolume()
	{
		return originalVolume - processedVolume;
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
	{	//
		//if(this.getStatus() == 0)
		//{
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
		this.setOriginalVolume(order.getOriginalVolume());
		this.setProcessedVolume(order.getProcessedVolume());
		this.setPrice(order.getPrice());
		this.setDealingPrice(order.getDealingPrice());
		this.setOpenTime(order.getOpenTime());
		this.setStatus(order.getStatus());
		return this;
	}
	
	public void updatePendingOrderList(ArrayList<Order> pOList)
	{
		if(this.getStatus() == 1 || this.getStatus() == 2)
		{
			if(this.getOrderType() == 1)
			{
				int i = 0;
				while(i < pOList.size())
				{
					if(pOList.get(i).getOrderID().equals(this.getOrderID()))
					{
						pOList.get(i).setDealingPrice(this.getDealingPrice());
						pOList.get(i).setProcessedVolume(this.getProcessedVolume());
						pOList.get(i).setStatus(this.getStatus());
					}
					i++;
				}
			}
			else if(this.getOrderType() == 2)
			{
				int i = 0;
				while(i < pOList.size())
				{
					if(pOList.get(i).getOrderID().equals(this.getOrderID()))
					{
						int updatedVolume = pOList.get(i).getProcessedVolume() + this.getProcessedVolume();
						pOList.get(i).setProcessedVolume(updatedVolume);
						pOList.get(i).setStatus(this.getStatus());
					}
					i++;
				}
			}
		}
		else if(this.getStatus() == 3 || this.getStatus() == 4)
		{
			ListIterator<Order> it = pOList.listIterator();				
			while(it.hasNext())
			{
				if(it.next().getOrderID().equals(this.getOrderID()))
				{
					it.remove();
				}
			}
		}		
	}
	
	public void updateQueue(PriorityQueue<Order> oBList)
	{
		Iterator<Order> it = oBList.iterator();
		
		while(it.hasNext())
		{
			if(it.next().getOrderID().equals(this.getOrderID()))
			{
				it.remove();
			}
		}
	}
	
	public String toString()
	{
		if(this.isNewOrder())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getOriginalVolume() + " " + OrderStatus.getOrderStatus(this));
			}
			else
				return (OrderSide.getSide(this).toString() + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getOriginalVolume() + " Price " + this.getPrice() + " " + OrderStatus.getOrderStatus(this));
		}
		else if(this.isFilled()||this.isPartiallyFilled()||this.isCanceled())
		{
			if(this.isMarketOrder())
			{
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume " + this.getProcessedVolume() + "/" + this.getOriginalVolume() + " with DealingPrice " + this.getDealingPrice() + " " + OrderStatus.getOrderStatus(this));
			}
			else
				return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume "+ this.getProcessedVolume() + "/" + this.getOriginalVolume()  + " with DealingPrice " + this.getPrice() + " " + OrderStatus.getOrderStatus(this));
		}
		else
		{
			return (OrderSide.getSide(this).toString()  + " OrderID " + this.getOrderID() + " " + this.getSymbol() + " " + OrderType.getOrderType(this).toString() + " Volume "+ this.getOriginalVolume() + " " + OrderStatus.getOrderStatus(this));
		}		
	}
}
