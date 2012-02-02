package orderBookUpdated51_4;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class InvestorAgentII extends Agent
{
	private int id = 0;
	private ArrayList<Order> pendingOrderListII = new ArrayList<Order>();
	private LinkedList<Order> buySideOrdersII = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrdersII = new LinkedList<Order>();
	private RandomGenerator rg = new RandomGenerator();

	protected void setup()
	{
		System.out.println("This is updated51_4 " + getAID().getName());
			        
        getContentManager().registerLanguage(MarketAgent.codecI, FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(MarketAgent.ontology);

    	SequentialBehaviour LogonMarket = new SequentialBehaviour();
    	LogonMarket.addSubBehaviour(new TradingRequest());
    	LogonMarket.addSubBehaviour(new TradingPermission());
    	LogonMarket.addSubBehaviour(new NoisyTradeBehaviour(this,1000));
    		
    	addBehaviour(LogonMarket);
    	addBehaviour(new LocalOrderManager());
	 }
	
	private class TradingRequest extends OneShotBehaviour
	{
		public void action() 
		{
			buySideOrdersII.addAll(MarketAgent.buySideQueue);
    		Collections.sort(buySideOrdersII);
    		sellSideOrdersII.addAll(MarketAgent.sellSideQueue);
    		Collections.sort(sellSideOrdersII);
    		
    		System.out.println(getAID().getLocalName() + " LocalBuyOrdersII: " + buySideOrdersII);
    		System.out.println(getAID().getLocalName() + " LocalSellOrdersII: " + sellSideOrdersII);
    		
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
	
	private class NoisyTradeBehaviour extends TickerBehaviour
	{	
		public NoisyTradeBehaviour(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick()
		{
			try
			{
				int randomTime = (int)(1000 + Math.random()*1000);
				
				if(buySideOrdersII.size() > 0 && sellSideOrdersII.size() > 0)
				{
					Order newOrder = new Order();					
					newOrder.setOrderType(rg.randomType(40));				
					if(newOrder.getOrderType() == 1)
					{
						newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
						newOrder.setSymbol("GOOGLE");
						newOrder.setSide(rg.randomSide(50));
						newOrder.setVolume(rg.randomVolume(10, 190));
						newOrder.setOpenTime(System.currentTimeMillis());
					}
					else if(newOrder.getOrderType() == 2)
					{
						newOrder.setSide(rg.randomSide(50));
						
						if(newOrder.getSide() == 1)
						{
							newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
							newOrder.setSymbol("GOOGLE");
							newOrder.setVolume(rg.randomVolume(10, 190));
							newOrder.setPrice(rg.randomBidPrice(buySideOrdersII.get(0).getPrice()));
							newOrder.setOpenTime(System.currentTimeMillis());
						}
						else
						{
							newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
							newOrder.setSymbol("GOOGLE");
							newOrder.setVolume(rg.randomVolume(10, 200));
							newOrder.setPrice(rg.randomAskPrice(sellSideOrdersII.get(0).getPrice()));
							newOrder.setOpenTime(System.currentTimeMillis());
						}	
					}
					
					pendingOrderListII.add(newOrder);
					System.out.println("Pending ordersII " + pendingOrderListII);
					
					Action action = new Action(MarketAgent.marketAID, newOrder);
					ACLMessage orderRequestMsg = new ACLMessage(ACLMessage.CFP);
					orderRequestMsg.addReceiver(MarketAgent.marketAID);
					orderRequestMsg.setOntology(MarketAgent.ontology.getName());
					orderRequestMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					myAgent.getContentManager().fillContent(orderRequestMsg, action);
					myAgent.send(orderRequestMsg);	
					
					Strategy cancelStrategy = new Strategy();
					ArrayList<Order> temp = new ArrayList<Order>();
					temp.addAll(cancelStrategy.cancelOrders(pendingOrderListII, (buySideOrdersII.get(0).getPrice() + sellSideOrdersII.get(0).getPrice())/2, 0.8));
					if(temp.size() > 0)
					{
						int i = 0;
						while(i < temp.size())
						{
							Action actionI = new Action(MarketAgent.marketAID, temp.get(i));
							ACLMessage cancelRequestMsg = new ACLMessage(ACLMessage.CANCEL);
							cancelRequestMsg.addReceiver(MarketAgent.marketAID);
							cancelRequestMsg.setOntology(MarketAgent.ontology.getName());
							cancelRequestMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
							myAgent.getContentManager().fillContent(cancelRequestMsg, actionI);
							myAgent.send(cancelRequestMsg);	
							System.out.println("Cancel " + temp.get(i));
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
				    	orderInfomation.updateLocalOrderbook(buySideOrdersII, sellSideOrdersII);
				    	
				    	if(orderInfomation.getOrderID().contains(getLocalName()))
				    	{
				    		orderInfomation.updatePendingOrderList(pendingOrderListII);
					    	System.out.println("Updated Pending ListII " + pendingOrderListII);
				    	}
				    }
				    	System.out.println(getAID().getLocalName() + " BuyOrdersII: " + buySideOrdersII);
				    	System.out.println(getAID().getLocalName() + " SellOrdersII: " + sellSideOrdersII);
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
