package orderBookUpdated21;

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
		System.out.println("This is updated21 " + getAID().getName());
		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
		
		addBehaviour(new OrderManageSystem());
		addBehaviour(new PriceResponder());
	}
	
	private class OrderManageSystem extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and( MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName()) ); 
			ACLMessage orderRequestMsg = blockingReceive(mt);
			try
			{
				ContentElement ce = null;
				ce = getContentManager().extractContent(orderRequestMsg);	
				Action act = (Action) ce;
				Order newOrder = (Order) act.getAction();
				
				System.out.println("~~~buy~~~ " + buySideOrder);
				System.out.println("~~~sell~~~ " + sellSideOrder);
				
				if(orderRequestMsg.getPerformative() == ACLMessage.REQUEST)
				{	
					if(newOrder.getSide() == 1)
					{
						buySideOrder.add(newOrder);
						BuySideMatch buyOrderMatch = new BuySideMatch();
						buyOrderMatch.setBQ(buySideOrder);
						buyOrderMatch.setSQ(sellSideOrder);				
						PriorityQueue<Order> tempBuyOrder = new PriorityQueue<Order>();
						tempBuyOrder.addAll(buyOrderMatch.matchOrder());

						while(tempBuyOrder.peek()!=null)
						{
							if(tempBuyOrder.peek().getStatus() == 1)
							{
								currentPrice = tempBuyOrder.peek().getPrice();
								ACLMessage replyOrderMsg = orderRequestMsg.createReply();
								Action action = new Action(orderRequestMsg.getSender(),tempBuyOrder.poll());
								replyOrderMsg.setPerformative(ACLMessage.INFORM);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								myAgent.send(replyOrderMsg);
							}
							else if(tempBuyOrder.peek().getStatus() == 2)
							{
								currentPrice = tempBuyOrder.peek().getPrice();
								ACLMessage replyOrderMsg = orderRequestMsg.createReply();
								Action action = new Action(orderRequestMsg.getSender(),tempBuyOrder.poll());
								replyOrderMsg.setPerformative(ACLMessage.PROPOSE);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								myAgent.send(replyOrderMsg);
							}
							else if(tempBuyOrder.peek().getStatus() == 3)
							{
								ACLMessage replyOrderMsg = orderRequestMsg.createReply();
								Action action = new Action(orderRequestMsg.getSender(),tempBuyOrder.poll());
								replyOrderMsg.setPerformative(ACLMessage.REJECT_PROPOSAL);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								myAgent.send(replyOrderMsg);
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
						
						while(tempSellOrder.peek()!=null)
						{
							if(tempSellOrder.peek().getStatus() == 1)
							{
								currentPrice = tempSellOrder.peek().getPrice();
								ACLMessage replyOrderMsg = orderRequestMsg.createReply();
								Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.poll());
								replyOrderMsg.setPerformative(ACLMessage.INFORM);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								myAgent.send(replyOrderMsg);
							}
							else if(tempSellOrder.peek().getStatus() == 2)
							{
								currentPrice = tempSellOrder.peek().getPrice();
								ACLMessage replyOrderMsg = orderRequestMsg.createReply();
								Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.poll());
								replyOrderMsg.setPerformative(ACLMessage.PROPOSE);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								myAgent.send(replyOrderMsg);
							}
							else if(tempSellOrder.peek().getStatus() == 3)
							{
								ACLMessage replyOrderMsg = orderRequestMsg.createReply();
								Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.poll());
								replyOrderMsg.setPerformative(ACLMessage.REJECT_PROPOSAL);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								myAgent.send(replyOrderMsg);
							}
						}
					}
				}
				else if(orderRequestMsg.getPerformative() == ACLMessage.CANCEL)
				{
					if(newOrder.getSide() == 1)
			        {
						ui.updateQueue(buySideOrder, newOrder);
			        }
					else
					{
						ui.updateQueue(sellSideOrder, newOrder);
					}
					
					ACLMessage replyCancelMsg = orderRequestMsg.createReply();
					Action action = new Action(orderRequestMsg.getSender(),newOrder);
					replyCancelMsg.setPerformative(ACLMessage.CONFIRM);
					replyCancelMsg.setOntology(ontology.getName());
					replyCancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					myAgent.getContentManager().fillContent(replyCancelMsg, action);
				    //System.out.println(cancelMsgReply);
			        myAgent.send(replyCancelMsg);
				}
			}
			catch(CodecException ce){
				ce.printStackTrace();
			}
			catch(OntologyException oe){
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
				myAgent.send(replyPriceMsg);
			}
			else
				block();
		}
	}
}

