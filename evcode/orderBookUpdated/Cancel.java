package orderBookUpdated12;

import jade.content.AgentAction;

public class Cancel implements AgentAction{
	private int orderID;
	
	public Cancel()
	{
		
	}
	
	public void setOrderID(int orderID)
	{
		this.orderID = orderID;
	}
	public int getOrderID()
	{
		return orderID;
	}

}
