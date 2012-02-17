package orderBookUpdated52_5;

import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.LinkedList;

public class NoiseTraderBehaviour extends TickerBehaviour
{	
	private int id = 0;
	private LinkedList<Order> buySideOrders;
	private LinkedList<Order> sellSideOrders;
	private ArrayList<Order> pendingOrderList;
	
	public NoiseTraderBehaviour(Agent a, long period, LinkedList<Order> buySideOrders, LinkedList<Order> sellSideOrders, ArrayList<Order> pendingOrderList) 
	{
		super(a, period);
		this.buySideOrders = buySideOrders;
		this.sellSideOrders = sellSideOrders;
		this.pendingOrderList = pendingOrderList;
	}

	protected void onTick()
	{
		try
		{
			int randomTime = (int)(500 + Math.random()*1500);
			
			if(buySideOrders.size() > 0 && sellSideOrders.size() > 0)
			{
				String orderID = myAgent.getAID().getLocalName() + " " + String.valueOf(id++);
				Order newOrder = new InitializeOrder().initNoiseOrder(buySideOrders.get(0).getPrice(), sellSideOrders.get(0).getPrice(), 40, 50, orderID);
				
				Action action = new Action(MarketAgent.marketAID, newOrder);
				ACLMessage orderRequestMsg = new Messages(ACLMessage.CFP, MarketAgent.marketAID).createMessage();
				myAgent.getContentManager().fillContent(orderRequestMsg, action);
				myAgent.send(orderRequestMsg);					
				pendingOrderList.add(newOrder);
				//System.out.println(myAgent.getLocalName() + " Pending orders " + pendingOrderList);
				
				ArrayList<Order> cancelList = new ManageOrders().cancelOrders(pendingOrderList, (buySideOrders.get(0).getPrice() + sellSideOrders.get(0).getPrice())/2, 0.6);
				if(cancelList.size() > 0)
				{
					int i = 0;
					while(i < cancelList.size())
					{
						Action actionI = new Action(MarketAgent.marketAID, cancelList.get(i));
						ACLMessage cancelRequestMsg = new Messages(ACLMessage.CANCEL, MarketAgent.marketAID).createMessage();
						myAgent.getContentManager().fillContent(cancelRequestMsg, actionI);
						myAgent.send(cancelRequestMsg);	
						//System.out.println(myAgent.getLocalName() + " Cancel " + cancelList.get(i));
						i++;
					}	
				}			
			}
		
			reset(randomTime);
		}
		catch(Exception ex){
			System.out.println(ex);
		}
	}
}
