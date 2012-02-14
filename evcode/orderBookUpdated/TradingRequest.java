package orderBookUpdated52_5;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;
import java.util.Collections;

public class TradingRequest extends OneShotBehaviour
{
	LinkedList<Order> buySideOrders;
	LinkedList<Order> sellSideOrders;
	
	public TradingRequest(LinkedList<Order> buySideOrders, LinkedList<Order> sellSideOrders)
	{
		this.buySideOrders = buySideOrders;
		this.sellSideOrders = sellSideOrders;
	}
	public void action() 
	{
		buySideOrders.addAll(MarketAgent.buySideQueue);
		Collections.sort(buySideOrders);
		sellSideOrders.addAll(MarketAgent.sellSideQueue);
		Collections.sort(sellSideOrders);
		
		System.out.println(myAgent.getLocalName() + " LocalBuyOrders: " + buySideOrders.size());
		System.out.println(myAgent.getLocalName() + " LocalSellOrders: " + sellSideOrders.size());
		
		ACLMessage tradingRequestMsg = new ACLMessage(ACLMessage.REQUEST);
		tradingRequestMsg.setConversationId("TradingRequest");
		tradingRequestMsg.setContent("ReadyToStart");
		tradingRequestMsg.addReceiver(MarketAgent.marketAID);
		myAgent.send(tradingRequestMsg);				
	}	
}
