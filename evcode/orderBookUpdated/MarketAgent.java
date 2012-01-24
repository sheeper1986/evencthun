package orderBookUpdated50_2;

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
	public static final AID marketAID = new AID("MarketAgent", AID.ISLOCALNAME);
	public static final Ontology ontology = OrderBookOntology.getInstance();
	public static final Codec codecI = new SLCodec();
	public static final Codec codecII = new SLCodec();
	private PriorityQueue<Order> buySideOrders = new PriorityQueue<Order>();
	private PriorityQueue<Order> sellSideOrders = new PriorityQueue<Order>();
	//public static double currentPrice;
	
	protected void setup()
	{
		System.out.println("This is updated50_1 " + getAID().getName());
		getContentManager().registerLanguage(codecI, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerLanguage(codecII, FIPANames.ContentLanguage.FIPA_SL1);
		getContentManager().registerOntology(ontology);
		
		InitializeOrder io = new InitializeOrder();
		io.initializeBuyOrder(buySideOrders, sellSideOrders, 10000);
		System.out.println("~~~buy~~~ " + buySideOrders.size());
		System.out.println("~~~sell~~~ " + sellSideOrders.size());
		addBehaviour(new InitOrderbookResponder());
		addBehaviour(new OrderManageSystem());
	}
	
	private class OrderManageSystem extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName())); 
			ACLMessage orderRequestMsg = receive(mt);
			if(orderRequestMsg != null)
			{
				try
				{
					ContentElement ce = null;
					ce = getContentManager().extractContent(orderRequestMsg);	
					Action act = (Action) ce;
					Order newOrder = (Order) act.getAction();
					

					
					if(orderRequestMsg.getPerformative() == ACLMessage.CFP)
					{	
						if(newOrder.getSide() == 1)
						{
							buySideOrders.add(newOrder);
							LimitOrderBook orderbook = new LimitOrderBook();
							ArrayList<Order> tempBuyOrder = new ArrayList<Order>();
							tempBuyOrder.addAll(orderbook.matchMechanism(buySideOrders,sellSideOrders));

							int i = 0;
							while(i < tempBuyOrder.size())
							{
								if(tempBuyOrder.get(i).getStatus() == 1)
								{
									//currentPrice = tempBuyOrder.get(i).getDealingPrice();
									Action action = new Action(orderRequestMsg.getSender(), tempBuyOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);		
									//replyOrderMsg.setPerformative(ACLMessage.INFORM);
									//replyOrderMsg.setReplyWith("ProcessedOrders");
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								else if(tempBuyOrder.get(i).getStatus() == 2)
								{
									//currentPrice = tempBuyOrder.get(i).getDealingPrice();
									Action action = new Action(orderRequestMsg.getSender(),tempBuyOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									//replyOrderMsg.setPerformative(ACLMessage.PROPOSE);
									//replyOrderMsg.setReplyWith("ProcessedOrders");
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
									//replyOrderMsg.setReplyWith("ProcessedOrders");
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								i++;
							}
						}
						else if (newOrder.getSide() == 2)
						{
							sellSideOrders.add(newOrder);
							LimitOrderBook orderbook = new LimitOrderBook();
							//orderbook.setBuySideOrderbook(buySideOrders);
							//orderbook.setSellSideOrderbook(sellSideOrders);				
							ArrayList<Order> tempSellOrder = new ArrayList<Order>();
							tempSellOrder.addAll(orderbook.matchMechanism(buySideOrders, sellSideOrders));

							int i = 0;
							while(i < tempSellOrder.size())
							{
								if(tempSellOrder.get(i).getStatus() == 1)
								{
									//currentPrice = tempSellOrder.get(i).getDealingPrice();
									Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
									//replyOrderMsg.setPerformative(ACLMessage.INFORM);
									//replyOrderMsg.setReplyWith("ProcessedOrders");
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									replyOrderMsg.addReceiver(orderRequestMsg.getSender());
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									myAgent.send(replyOrderMsg);
								}
								else if(tempSellOrder.get(i).getStatus() == 2)
								{
									//currentPrice = tempSellOrder.get(i).getDealingPrice();
									Action action = new Action(orderRequestMsg.getSender(),tempSellOrder.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									//replyOrderMsg.setPerformative(ACLMessage.PROPOSE);
									//replyOrderMsg.setReplyWith("ProcessedOrders");
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
									//replyOrderMsg.setReplyWith("ProcessedOrders");
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
						//replyCancelMsg.setReplyWith("ProcessedOrders");
						replyCancelMsg.setOntology(ontology.getName());
						replyCancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
						replyCancelMsg.addReceiver(orderRequestMsg.getSender());
						myAgent.getContentManager().fillContent(replyCancelMsg, action);
				        myAgent.send(replyCancelMsg);
					}
					//System.out.println("~~~buy~~~ " + buySideOrders);
					//System.out.println("~~~sell~~~ " + sellSideOrders);
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
	
	private class InitOrderbookResponder extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId("InitializeOrders"));
			ACLMessage initOrderbookMsg = receive(pt);

			if(initOrderbookMsg != null)
			{
				System.out.println(initOrderbookMsg);
				ArrayList<Order> pendingInitList = new ArrayList<Order>();
				pendingInitList.addAll(buySideOrders);
				pendingInitList.addAll(sellSideOrders);
				
				int i = 0;
				while(i < pendingInitList.size())
				{
					try
					{
						Action action = new Action(initOrderbookMsg.getSender(), pendingInitList.get(i));
					    ACLMessage replyInitMsg = new ACLMessage(ACLMessage.AGREE);
					    //replyInitMsg.setReplyWith("InitializeOrders");
					    replyInitMsg.setOntology(ontology.getName());
					    replyInitMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL1);
					    replyInitMsg.addReceiver(initOrderbookMsg.getSender());
					    myAgent.getContentManager().fillContent(replyInitMsg, action);
					    myAgent.send(replyInitMsg);
					    i++;
					}
					catch (CodecException e) {
						e.printStackTrace();
						} 
					catch (OntologyException e) {
						e.printStackTrace();
						}
				}
			}
			else
				block();
		}
	}
}

