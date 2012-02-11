package orderBookUpdated52_2;

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

public class NoiseTraderII extends Agent
{
	private int id = 0;
	private ArrayList<Order> pendingOrderListII = new ArrayList<Order>();
	private LinkedList<Order> buySideOrdersII = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrdersII = new LinkedList<Order>();

	protected void setup()
	{
		System.out.println("This is updated52_2 " + getAID().getName());
			        
        getContentManager().registerLanguage(MarketAgent.codec, FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(MarketAgent.ontology);

    	SequentialBehaviour LogonMarket = new SequentialBehaviour();
    	LogonMarket.addSubBehaviour(new TradingRequest(buySideOrdersII,sellSideOrdersII));
    	LogonMarket.addSubBehaviour(new RequestApproved());
    	LogonMarket.addSubBehaviour(new NoisyTradeBehaviour(this,2000));
    		
    	addBehaviour(LogonMarket);
    	addBehaviour(new LocalOrderManager());
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
				int randomTime = (int)(500 + Math.random()*1500);
				
				if(buySideOrdersII.size() > 0 && sellSideOrdersII.size() > 0)
				{
					String orderID = myAgent.getAID().getLocalName()+String.valueOf(id++);
					Order newOrder = new InitializeOrder().initNoiseOrder(buySideOrdersII.get(0).getPrice(), sellSideOrdersII.get(0).getPrice(), 40, 50, orderID);
					
					Action action = new Action(MarketAgent.marketAID, newOrder);
					ACLMessage orderRequestMsg = new Messages(ACLMessage.CFP, MarketAgent.marketAID).createMessage();
					myAgent.getContentManager().fillContent(orderRequestMsg, action);
					myAgent.send(orderRequestMsg);
					
					pendingOrderListII.add(newOrder);
					//System.out.println("Pending ordersII " + pendingOrderListII);
					
					ArrayList<Order> cancelList = new ManageOrders().cancelOrders(pendingOrderListII, (buySideOrdersII.get(0).getPrice() + sellSideOrdersII.get(0).getPrice())/2, 0.6);
					if(cancelList.size() > 0)
					{
						int i = 0;
						while(i < cancelList.size())
						{
							Action actionI = new Action(MarketAgent.marketAID, cancelList.get(i));
							ACLMessage cancelRequestMsg = new Messages(ACLMessage.CANCEL, MarketAgent.marketAID).createMessage();
							myAgent.getContentManager().fillContent(cancelRequestMsg, actionI);
							myAgent.send(cancelRequestMsg);	
							//System.out.println(getLocalName() + " Cancel " + cancelList.get(i));
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
				    	new ManageOrders(orderInfomation).updateLocalOrderbook(buySideOrdersII, sellSideOrdersII);
				    	
				    	if(orderInfomation.getOrderID().contains(getLocalName()))
				    	{
				    		new ManageOrders(orderInfomation).updatePendingOrderList(pendingOrderListII);
					    	//System.out.println("Updated Pending ListII " + pendingOrderListII);
				    	}
				    }
				    	//System.out.println(getAID().getLocalName() + " BuyOrdersII: " + buySideOrdersII.size());
				    	//System.out.println(getAID().getLocalName() + " SellOrdersII: " + sellSideOrdersII.size());
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
