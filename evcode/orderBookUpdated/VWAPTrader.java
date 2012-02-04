package orderBookUpdated52_1;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class VWAPTrader extends Agent
{
	private int id = 0;
	private ArrayList<Order> pendingOrderListIV = new ArrayList<Order>();
	private LinkedList<Order> buySideOrdersIV = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrdersIV = new LinkedList<Order>();
	private RandomGenerator rg = new RandomGenerator();

	protected void setup()
	{
		System.out.println("This is updated52_1 " + getAID().getName());
			        
        getContentManager().registerLanguage(MarketAgent.codecI, FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(MarketAgent.ontology);

    	SequentialBehaviour LogonMarket = new SequentialBehaviour();
    	LogonMarket.addSubBehaviour(new TradingRequest());
    	LogonMarket.addSubBehaviour(new TradingPermission());
    	LogonMarket.addSubBehaviour(new vwapTradeBehaviour(this,1000));
    		
    	addBehaviour(LogonMarket);
    	addBehaviour(new LocalOrderManager());
	 }
	
	private class TradingRequest extends OneShotBehaviour
	{
		public void action() 
		{
			buySideOrdersIV.addAll(MarketAgent.buySideQueue);
    		Collections.sort(buySideOrdersIV);
    		sellSideOrdersIV.addAll(MarketAgent.sellSideQueue);
    		Collections.sort(sellSideOrdersIV);
    		
    		System.out.println(getAID().getLocalName() + " LocalBuyOrders: " + buySideOrdersIV.size());
    		System.out.println(getAID().getLocalName() + " LocalSellOrders: " + sellSideOrdersIV.size());
    		
			ACLMessage tradingRequestMsg = new ACLMessage(ACLMessage.REQUEST);
			tradingRequestMsg.setConversationId("TradingRequest");
			tradingRequestMsg.setContent("ReadyToStart");
			tradingRequestMsg.addReceiver(MarketAgent.marketAID);
			myAgent.send(tradingRequestMsg);				
		}	
	}
	
	private class TradingPermission extends Behaviour
	{
		int i = 0;
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
					MessageTemplate.MatchConversationId("TradingPermission")); 
            ACLMessage tradingRequestMsg = receive(mt);
            
            if(tradingRequestMsg != null)
            {
    			System.out.println(getAID().getLocalName() + " Start Trading...... ");
    			i++;
            }
		}

		public boolean done() {
			if(i < 1)
			{
				return false;
			}
			else
				return true;
		}
	}
	
	private class vwapTradeBehaviour extends TickerBehaviour
	{

		public vwapTradeBehaviour(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onTick() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	private class LocalOrderManager extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), 
					MessageTemplate.MatchOntology(MarketAgent.ontology.getName())); 
			//blockingReceive() cannot use here, because it keeps messages, cyclicBehaviour will not stop
			ACLMessage processedOrderMsg = receive(mt);

			if(processedOrderMsg != null)
			{
				try
				{
					ContentElement ce = null;
				    ce = getContentManager().extractContent(processedOrderMsg);	
				    Action act = (Action) ce;
				    Order orderInfomation = (Order) act.getAction();
				    //System.out.println(orderInfomation);
				    
				    if(processedOrderMsg.getPerformative() == ACLMessage.INFORM)
				    {
				    	orderInfomation.updateLocalOrderbook(buySideOrdersIV, sellSideOrdersIV);
				    	
				    	if(orderInfomation.getOrderID().contains(getLocalName()))
				    	{
				    		orderInfomation.updatePendingOrderList(pendingOrderListIV);
					    	System.out.println("Updated Pending List " + pendingOrderListIV);
				    	}
				    }
				    	System.out.println(getAID().getLocalName() + " BuyOrders: " + buySideOrdersIV.size());
				    	System.out.println(getAID().getLocalName() + " SellOrders: " + sellSideOrdersIV.size());
				}	
				catch(CodecException ce){
					ce.printStackTrace();
					}
				catch(OntologyException oe){
					oe.printStackTrace();
					}
				}
				else
					block();
			}
		}
	}

