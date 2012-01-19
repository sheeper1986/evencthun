package orderBookUpdated29_9;

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
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class InvestorAgentSecond extends Agent
{
	private AID MarketAgent = new AID("MarketAgent", AID.ISLOCALNAME);
	private Ontology ontology = OrderBookOntology.getInstance();
	private Codec codec = new SLCodec();
	private int id = 0;
	private TradingAlgorithm tradeAlgos = new TradingAlgorithm();
	private ArrayList<Order> pendingOrderListII = new ArrayList<Order>();
	private ArrayList<Asset> assetListII = new ArrayList<Asset>();
	private Asset asset = new Asset();
	private double fundAvailable;
	
	protected void setup()
	{
		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
		
		System.out.println("This is updated29_8 " + getAID().getName());
		//ParallelBehaviour pb = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ANY);
		//pb.addSubBehaviour();
		addBehaviour(new RandomGenerator(this, 5000));
		addBehaviour(new ProcessedOrderManager());
		addBehaviour(new PriceChecker(this, 3000));
		addBehaviour(new AutoCancel());
	 }
	
	private class RandomGenerator extends TickerBehaviour
	{	
		public RandomGenerator(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick()
		{
			int randomVolume = (int)(30+Math.random()*70);
			int randomSide = (int)(1+Math.random()*2);
			int randomTime = (int)(500 + Math.random()*1000);
			int randomType = (int)(1+Math.random()*2);
			int randomSellPrice = (int)(50+Math.random()*6);
			int randomBuyPrice = (int)(45+Math.random()*6);
			
		    Order newOrder = new Order();
			try
			{
				newOrder.setType(randomType);
				
				if(newOrder.getType() == 1)
				{
					newOrder.setOrderID(getAID().getLocalName()+String.valueOf(id++));
					newOrder.setSymbol("GOOGLE");
					newOrder.setSide(randomSide);
					newOrder.setVolume(randomVolume);
					newOrder.setOpenTime(System.currentTimeMillis());
				}
				else if(newOrder.getType() == 2)
				{
					newOrder.setSide(randomSide);
					
					if(newOrder.getSide() == 1)
					{
						newOrder.setOrderID(getAID().getLocalName()+String.valueOf(id++));
						newOrder.setSymbol("GOOGLE");
						newOrder.setVolume(randomVolume);
						newOrder.setPrice(randomBuyPrice);
						newOrder.setOpenTime(System.currentTimeMillis());
					}
					else if(newOrder.getSide() == 2)
					{
						newOrder.setOrderID(getAID().getLocalName()+String.valueOf(id++));
						newOrder.setSymbol("GOOGLE");
						newOrder.setVolume(randomVolume);
						newOrder.setPrice(randomSellPrice);
						newOrder.setOpenTime(System.currentTimeMillis());
					}	
				}
				
				pendingOrderListII.add(newOrder);
				System.out.println("Pending ordersII " + pendingOrderListII);
				
				Action action = new Action(MarketAgent, newOrder);
				ACLMessage orderRequestMsg = new ACLMessage(ACLMessage.REQUEST);
				orderRequestMsg.addReceiver(MarketAgent);
				orderRequestMsg.setOntology(ontology.getName());
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
	
	private class ProcessedOrderManager extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName())); 
			//blockingReceive() cannot use here, because it keeps messages, cyclicBehaviour will not stop
			ACLMessage processedOrderMsg = receive(mt);
		
			if(processedOrderMsg!=null){
				try
				{
					ContentElement ce = null;
					ce = getContentManager().extractContent(processedOrderMsg);	
					Action act = (Action) ce;
					Order processedOrder = (Order) act.getAction();
					
					if(processedOrderMsg.getPerformative() == ACLMessage.INFORM)
					{
						System.out.println("Filled !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
						//asset.updateAssetList(assetList, processedOrder);
					}
					
					else if(processedOrderMsg.getPerformative() == ACLMessage.PROPOSE)
					{
						System.out.println("Great PartlyFilled !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
						//asset.updateAssetList(assetList, processedOrder);
					}
					
					else if(processedOrderMsg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
					{
						System.out.println("Rejected !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
					}
					
					else if(processedOrderMsg.getPerformative() == ACLMessage.CONFIRM)
					{
						System.out.println("Cancel Successful !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
					}
					
					System.out.println("Updated Pending ListII " + pendingOrderListII);
					//System.out.println("Updated Asset List " + assetList);
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
	
	private class PriceChecker extends TickerBehaviour
	{
		public PriceChecker(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick()
		{
			try
			{
				ACLMessage checkPriceMsg = new ACLMessage(ACLMessage.REQUEST);
				checkPriceMsg.setConversationId("CheckPrice");
				checkPriceMsg.addReceiver(MarketAgent);
				myAgent.send(checkPriceMsg);	
			}
			catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	private class AutoCancel extends CyclicBehaviour
	{
		public void action() 
		{
			MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchConversationId("PriceInform"));
			ACLMessage receiPrice = receive(pt);
			
			if(receiPrice != null)
			{
				double currentPrice = Double.parseDouble(receiPrice.getContent());
				if(pendingOrderListII.size()>5 && currentPrice != 0)
				{
					ArrayList<Order> temp = new ArrayList();
					temp.addAll(tradeAlgos.matchedOrderSpread(pendingOrderListII, currentPrice, 3));
					if( temp != null)
					{
						int i = 0;
						while (i < temp.size())
						{
							try
							{
								Action cancelAct = new Action(MarketAgent, temp.get(i));
								ACLMessage cancelMsg = new ACLMessage(ACLMessage.CANCEL);
								cancelMsg.addReceiver(MarketAgent);
								cancelMsg.setOntology(ontology.getName());
								cancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
                                myAgent.getContentManager().fillContent(cancelMsg, cancelAct);
								myAgent.send(cancelMsg);
								//temp.remove(i);
								i++;
							 }
							 catch (CodecException e){
								e.printStackTrace();
							 } 
							 catch (OntologyException e){
								e.printStackTrace();
							 }	
						 }
					}
				}
			}
			else
				block();	
		}
	}
}