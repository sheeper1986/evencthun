package orderBookUpdated18;

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

public class InvestorAgent extends Agent
{
	private AID CentralisedAgent = new AID("CentralisedAgent", AID.ISLOCALNAME);
	private Ontology ontology = OrderBookOntology.getInstance();
	private Codec codec = new SLCodec();
	private static int id = 0;
	private ArrayList<Order> proposedOrder = new ArrayList<Order>();
	private ArrayList<Order> filledOrder = new ArrayList<Order>();
	private UpdateInventory ui = new UpdateInventory();
	private static double currentPrice;
	
	protected void setup()
	{
		
		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
		
		System.out.println("This is updated18 " + getAID().getName());
		//ParallelBehaviour pb = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ANY);
		//pb.addSubBehaviour();
		addBehaviour(new RandomGenerator(this, 5000));
		addBehaviour(new MessageManager());
		addBehaviour(new PriceChecker(this, 3000));
		addBehaviour(new PriceReceiver());
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
			int randomTime = (int)(1000 + Math.random()*3000);
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
					newOrder.setVolume(randomSellPrice);
					newOrder.setOpenTime(System.nanoTime());
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
						newOrder.setOpenTime(System.nanoTime());
					}
					else if(newOrder.getSide() == 2)
					{
						newOrder.setOrderID(getAID().getLocalName()+String.valueOf(id++));
						newOrder.setSymbol("GOOGLE");
						newOrder.setVolume(randomVolume);
						newOrder.setPrice(randomSellPrice);
						newOrder.setOpenTime(System.nanoTime());
					}	
				}
				proposedOrder.add(newOrder);
				System.out.println("Proposed order " + proposedOrder);
				
				Action act = new Action(CentralisedAgent, newOrder);
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				
				msg.addReceiver(CentralisedAgent);
				msg.setOntology(ontology.getName());
				msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
				myAgent.getContentManager().fillContent(msg, act);
				myAgent.send(msg);	
				reset(randomTime);
			}
			catch(Exception ex)
			{
					System.out.println(ex);
			}
		}
	}
	
	private class MessageManager extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and( MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName()) ); 
			//blockingReceive() cannot use here, because it keeps messages, cyclicBehaviour will not stop
			ACLMessage receiMsgFromEx = receive(mt);
		
			if(receiMsgFromEx!=null){
				try
				{
					ContentElement ce = null;
					ce = getContentManager().extractContent(receiMsgFromEx);	
					Action act = (Action) ce;
					Order replyOrder = (Order) act.getAction();
					
					if(receiMsgFromEx.getPerformative() == ACLMessage.INFORM)
					{
						System.out.println("Filled !" + replyOrder);
						if(replyOrder.getType() == 1)
						{
							filledOrder.add(replyOrder);
							ui.updateList(proposedOrder, replyOrder);
						}
						else if(replyOrder.getType() == 2)
						{
							if(replyOrder.equalOrder(filledOrder) == null)
							{
								filledOrder.add(replyOrder);
								ui.updateList(proposedOrder, replyOrder);
							}
							else
							{
								int plusVolume = replyOrder.equalOrder(filledOrder).getVolume() + replyOrder.getVolume();
								replyOrder.equalOrder(filledOrder).setVolume(plusVolume);
								ui.updateList(proposedOrder, replyOrder);
							}	
						}
					}
					else if(receiMsgFromEx.getPerformative() == ACLMessage.PROPOSE)
					{
						System.out.println("Great PartlyFilled !" + replyOrder + " " + getAID().getName());
						if(replyOrder.getType() == 1)
						{
							filledOrder.add(replyOrder);
							ui.updateList(proposedOrder, replyOrder);
						}
						else if(replyOrder.getType() == 2)
						{
							if(replyOrder.equalOrder(filledOrder) == null)
							{
								filledOrder.add(replyOrder);
								int minusVolume = replyOrder.equalOrder(proposedOrder).getVolume() - replyOrder.getVolume();
								replyOrder.equalOrder(proposedOrder).setVolume(minusVolume);
							}
							else
							{
								int plusVolume = replyOrder.equalOrder(filledOrder).getVolume() + replyOrder.getVolume();
								replyOrder.equalOrder(filledOrder).setVolume(plusVolume);
								int minusVolume = replyOrder.equalOrder(proposedOrder).getVolume() - replyOrder.getVolume();
								replyOrder.equalOrder(proposedOrder).setVolume(minusVolume);
							}
								
						}
					}
					else if(receiMsgFromEx.getPerformative() == ACLMessage.REJECT_PROPOSAL)
					{
						System.out.println("Rejected !" + replyOrder + " " + getAID().getName());
						ui.updateList(proposedOrder, replyOrder);
					}
					System.out.println("Stock Inventory " + filledOrder);
				}
				
				catch(CodecException ce)
				{
					ce.printStackTrace();
				}
				catch(OntologyException oe)
				{
					oe.printStackTrace();
				}
				catch(Exception e)
				{
					System.out.println(e);
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
				checkPriceMsg.addReceiver(CentralisedAgent);
				
				myAgent.send(checkPriceMsg);	
			}
			catch(Exception ex)
			{
					System.out.println(ex);
			}
		}
	}
	
	private class PriceReceiver extends CyclicBehaviour
	{
		public void action() 
		{
			MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchConversationId("PriceInform"));
			ACLMessage receiPrice = receive(pt);
			if(receiPrice != null)
			{
					currentPrice = Double.parseDouble(receiPrice.getContent());
					//System.out.println("----- " +currentPrice);
			}
			else
				block();	
		}
	}
}