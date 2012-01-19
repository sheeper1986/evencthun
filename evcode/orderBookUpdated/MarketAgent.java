package orderBookUpdated29_9;

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

public class MarketAgent extends Agent
{
	private PriorityQueue<Order> buySideOrders = new PriorityQueue<Order>();
	private PriorityQueue<Order> sellSideOrders = new PriorityQueue<Order>();
	private Codec codec = new SLCodec();
	private Ontology ontology = OrderBookOntology.getInstance();
	private double currentPrice;
	
	protected void setup()
	{
		System.out.println("This is updated29_9 " + getAID().getName());
		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
		
		addBehaviour(new OrderManageSystem());
		addBehaviour(new PriceResponder());
	}
	
	private class OrderManageSystem extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName())); 
			ACLMessage orderRequestMsg = blockingReceive(mt);
			try
			{
				ContentElement ce = null;
				ce = getContentManager().extractContent(orderRequestMsg);	
				Action act = (Action) ce;
				Order newOrder = (Order) act.getAction();
				
				//System.out.println("~~~buy~~~ " + buySideOrder);
				//System.out.println("~~~sell~~~ " + sellSideOrder);
				
				if(orderRequestMsg.getPerformative() == ACLMessage.REQUEST)
				{	
					if(newOrder.getSide() == 1)
					{
						buySideOrders.add(newOrder);
						BuySideOrderMatch buySideMatch = new BuySideOrderMatch();
						buySideMatch.setOrderbook(buySideOrders, sellSideOrders);
						ArrayList<Order> tempBuyOrder = new ArrayList<Order>();
						tempBuyOrder.addAll(buySideMatch.matchBuyOrder());

						if(tempBuyOrder != null)
						{
							int i = 0;
							while(i < tempBuyOrder.size())
							{
								if(tempBuyOrder.get(i).getStatus() == 1)
								{
									currentPrice = tempBuyOrder.get(i).getPrice();
									Action action = new Action(orderRequestMsg.getSender(), tempBuyOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);		
									//replyOrderMsg.setPerformative(ACLMessage.INFORM);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								else if(tempBuyOrder.get(i).getStatus() == 2)
								{
									currentPrice = tempBuyOrder.get(i).getPrice();
									Action action = new Action(orderRequestMsg.getSender(),tempBuyOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.PROPOSE);
									//replyOrderMsg.setPerformative(ACLMessage.PROPOSE);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								else if(tempBuyOrder.get(i).getStatus() == 3)
								{
									Action action = new Action(orderRequestMsg.getSender(),tempBuyOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
									//replyOrderMsg.setPerformative(ACLMessage.REJECT_PROPOSAL);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								i++;
							}
						}
					}
					else if (newOrder.getSide() == 2)
					{
						sellSideOrders.add(newOrder);
						SellSideOrderMatch sellOrderMatch = new SellSideOrderMatch();
						sellOrderMatch.setOrderbook(sellSideOrders, buySideOrders);
						
						ArrayList<Order> tempSellOrder = new ArrayList<Order>();
						tempSellOrder.addAll(sellOrderMatch.matchSellOrder());
						
						if(tempSellOrder != null)
						{
							int i = 0;
							while(i < tempSellOrder.size())
							{
								if(tempSellOrder.get(i).getStatus() == 1)
								{
									currentPrice = tempSellOrder.get(i).getPrice();
									Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									//replyOrderMsg.setPerformative(ACLMessage.INFORM);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								else if(tempSellOrder.get(i).getStatus() == 2)
								{
									currentPrice = tempSellOrder.get(i).getPrice();
									Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.PROPOSE);
									//replyOrderMsg.setPerformative(ACLMessage.PROPOSE);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								else if(tempSellOrder.get(i).getStatus() == 3)
								{
									Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
									//replyOrderMsg.setPerformative(ACLMessage.REJECT_PROPOSAL);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								i++;
							}
						}
					}
				}
				else if(orderRequestMsg.getPerformative() == ACLMessage.CANCEL)
				{
					if(newOrder.getSide() == 1)
			        {
						newOrder.updateQueue(buySideOrders);
			        }
					else
					{
						newOrder.updateQueue(sellSideOrders);
					}
					newOrder.setStatus(4);
					//ACLMessage replyCancelMsg = orderRequestMsg.createReply();
					Action action = new Action(orderRequestMsg.getSender(),newOrder);
					ACLMessage replyCancelMsg = new ACLMessage(ACLMessage.CONFIRM);
					//replyCancelMsg.setPerformative(ACLMessage.CONFIRM);
					replyCancelMsg.setOntology(ontology.getName());
					replyCancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					replyCancelMsg.addReceiver(orderRequestMsg.getSender());
					myAgent.getContentManager().fillContent(replyCancelMsg, action);
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
				ACLMessage replyPriceMsg = new ACLMessage(ACLMessage.INFORM);
				//replyPriceMsg.setPerformative(ACLMessage.INFORM);
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

