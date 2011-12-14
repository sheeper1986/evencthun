package orderBookUpdated20;

import java.util.*;

import eugene.market.ontology.field.Side;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CentralisedAgent extends Agent
{
	private PriorityQueue<Order> buySideOrder = new PriorityQueue<Order>();
	private PriorityQueue<Order> sellSideOrder = new PriorityQueue<Order>();
	private Codec codec = new SLCodec();
	private Ontology ontology = OrderBookOntology.getInstance();
	private static double currentPrice;
	private UpdateInventory ui = new UpdateInventory();
	
	protected void setup()
	{
		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
		
		System.out.println("This is updated20 " + getAID().getName());
		
		addBehaviour(new OrderManageSystem());
		addBehaviour(new PriceResponder());
		//addBehaviour(new CancelResponder());
	}
	
	private class OrderManageSystem extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and( MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName()) ); 
			ACLMessage receiMsg = blockingReceive(mt);
			try
			{
				ContentElement ce = null;
				ce = getContentManager().extractContent(receiMsg);	
				Action act = (Action) ce;
				Order newOrder = (Order) act.getAction();
				
				if(receiMsg.getPerformative() == ACLMessage.REQUEST)
				{	
					if(newOrder.getSide() == 1)
					{
						buySideOrder.add(newOrder);
						BuySideMatch buyOrderMatch = new BuySideMatch();
						buyOrderMatch.setBQ(buySideOrder);
						buyOrderMatch.setSQ(sellSideOrder);				
						PriorityQueue<Order> tempBuyOrder = new PriorityQueue<Order>();
						tempBuyOrder.addAll(buyOrderMatch.matchOrder());
						ACLMessage reply = receiMsg.createReply();

						while(tempBuyOrder.peek()!=null)
						{
							if(tempBuyOrder.peek().getStatus() == 1)
							{
								currentPrice = tempBuyOrder.peek().getPrice();
								
								Action action = new Action(receiMsg.getSender(),tempBuyOrder.poll());
								reply.setPerformative(ACLMessage.INFORM);
								reply.setOntology(ontology.getName());
								reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(reply, action);
								myAgent.send(reply);
							}
							else if(tempBuyOrder.peek().getStatus() == 2)
							{
								currentPrice = tempBuyOrder.peek().getPrice();

								Action action = new Action(receiMsg.getSender(),tempBuyOrder.poll());
								reply.setPerformative(ACLMessage.PROPOSE);
								reply.setOntology(ontology.getName());
								reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(reply, action);
								myAgent.send(reply);
							}
							else if(tempBuyOrder.peek().getStatus() == 3)
							{

								Action action = new Action(receiMsg.getSender(),tempBuyOrder.poll());
								reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
								reply.setOntology(ontology.getName());
								reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(reply, action);
								myAgent.send(reply);
							}
						}
					}
					
					else if (newOrder.getSide() == 2)
					{
						sellSideOrder.add(newOrder);
						SellSideMatch sellOrderMatch = new SellSideMatch();
						sellOrderMatch.setBQ(buySideOrder);
						sellOrderMatch.setSQ(sellSideOrder);
						
						PriorityQueue<Order> tempSellOrder = new PriorityQueue<Order>();
						tempSellOrder.addAll(sellOrderMatch.matchOrder());
						ACLMessage reply = receiMsg.createReply();
						
						while(tempSellOrder.peek()!=null)
						{
							if(tempSellOrder.peek().getStatus() == 1)
							{
								currentPrice = tempSellOrder.peek().getPrice();
							
								Action action = new Action(receiMsg.getSender(),tempSellOrder.poll());
								reply.setPerformative(ACLMessage.INFORM);
								reply.setOntology(ontology.getName());
								reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(reply, action);
								myAgent.send(reply);
							}
							else if(tempSellOrder.peek().getStatus() == 2)
							{
								currentPrice = tempSellOrder.peek().getPrice();
								
								Action action = new Action(receiMsg.getSender(),tempSellOrder.poll());
								reply.setPerformative(ACLMessage.PROPOSE);
								reply.setOntology(ontology.getName());
								reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(reply, action);
								myAgent.send(reply);
							}
							else if(tempSellOrder.peek().getStatus() == 3)
							{
								Action action = new Action(receiMsg.getSender(),tempSellOrder.poll());
								reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
								reply.setOntology(ontology.getName());
								reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(reply, action);
								myAgent.send(reply);
							}
						}
					}
					
				}
				//else if(receiMsg.getPerformative() == ACLMessage.CANCEL)
				//{
					
				//}
			}
			catch(CodecException ce)
			{
				ce.printStackTrace();
			}
			catch(OntologyException oe)
			{
				oe.printStackTrace();
			}
		}
	}
	private class PriceResponder extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId("CheckPrice"));
			ACLMessage priceMsg = receive(pt);
			if(priceMsg != null)
			{

					ACLMessage replyPriceMsg = priceMsg.createReply();
					replyPriceMsg.setPerformative(ACLMessage.INFORM);
					replyPriceMsg.setConversationId("PriceInform");
					replyPriceMsg.setContent(String.valueOf(currentPrice));
					replyPriceMsg.addReceiver(priceMsg.getSender());
					//System.out.println(replyPriceMsg);
					myAgent.send(replyPriceMsg);
			}
			else
				block();
			
		}
	}
/*	private class CancelResponder extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate ct = MessageTemplate.and( MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName()) ); 
			ACLMessage cancelMsg = receive(ct);
			if(cancelMsg != null)
			{
				try
				{
					ContentElement ce = null;
					
					if(cancelMsg.getPerformative() == ACLMessage.CANCEL)
					{
						ce = getContentManager().extractContent(cancelMsg);	
						Action act = (Action) ce;
						Order cancelOrder = (Order) act.getAction();
						
						if(cancelOrder.getType() == 1)
				        {
							ui.updateQueue(buySideOrder, cancelOrder);
				        }
						else
						{
							ui.updateQueue(sellSideOrder, cancelOrder);
						}
						
						ACLMessage cancelMsgReply = cancelMsg.createReply();
				        cancelMsgReply.setPerformative(ACLMessage.INFORM);
				        cancelMsgReply.setConversationId("CancelOrder");
				        cancelMsgReply.setContent(cancelOrder.getOrderID());
				        cancelMsgReply.addReceiver(cancelMsg.getSender());
	
				        myAgent.send(cancelMsgReply);
				     }
				 }
				catch(CodecException ce)
				{
					ce.printStackTrace();
				}
				catch(OntologyException oe)
				{
					oe.printStackTrace();
				}
			}
			else
				block();
			}
		}*/
	}

