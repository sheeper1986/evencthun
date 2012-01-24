package orderBookUpdated50_2;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class RandomTradingBehaviourII extends TickerBehaviour
{	
	private int id = 0;
	
	public RandomTradingBehaviourII(Agent a, long period) 
	{
		super(a, period);
	}

	protected void onTick()
	{
		int randomVolume = (int)(30+Math.random()*70);
		int randomSide = (int)(1+Math.random()*2);
		int randomTime = (int)(1000 + Math.random()*1000);
		int randomType = (int)(1+Math.random()*2);
		int randomSellPrice = (int)(50+Math.random()*6);
		int randomBuyPrice = (int)(45+Math.random()*6);
		
	    Order newOrder = new Order();
		try
		{
			newOrder.setOrderType(randomType);
			
			if(newOrder.getOrderType() == 1)
			{
				newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
				newOrder.setSymbol("GOOGLE");
				newOrder.setSide(randomSide);
				newOrder.setOriginalVolume(randomVolume);
				newOrder.setOpenTime(System.currentTimeMillis());
			}
			else if(newOrder.getOrderType() == 2)
			{
				newOrder.setSide(randomSide);
				
				if(newOrder.getSide() == 1)
				{
					newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
					newOrder.setSymbol("GOOGLE");
					newOrder.setOriginalVolume(randomVolume);
					newOrder.setPrice(randomBuyPrice);
					newOrder.setOpenTime(System.currentTimeMillis());
				}
				else if(newOrder.getSide() == 2)
				{
					newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
					newOrder.setSymbol("GOOGLE");
					newOrder.setOriginalVolume(randomVolume);
					newOrder.setPrice(randomSellPrice);
					newOrder.setOpenTime(System.currentTimeMillis());
				}	
			}
			
			InvestorAgentSecond.pendingOrderListII.add(newOrder);
			System.out.println("Pending ordersII " + InvestorAgentSecond.pendingOrderListII);
			
			Action action = new Action(MarketAgent.marketAID, newOrder);
			ACLMessage orderRequestMsg = new ACLMessage(ACLMessage.CFP);
			orderRequestMsg.addReceiver(MarketAgent.marketAID);
			orderRequestMsg.setOntology(MarketAgent.ontology.getName());
			orderRequestMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
			myAgent.getContentManager().fillContent(orderRequestMsg, action);
			myAgent.send(orderRequestMsg);	
			reset(randomTime);
		}
		catch(Exception ex){
			System.out.println(ex);
		}
	}
}
