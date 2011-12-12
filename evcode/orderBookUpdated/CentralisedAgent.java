package orderBookUpdated15;

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
	
	protected void setup()
	{
		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
		
		System.out.println("This is updated15 " + getAID().getName());
		
		addBehaviour(new CyclicBehaviour(){
			public void action()
			{
				MessageTemplate mt = MessageTemplate.and( MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName()) ); 
				ACLMessage receiMsg = blockingReceive(mt);
				
				try
				{
					ContentElement ce = null;
					if(receiMsg.getPerformative() == ACLMessage.REQUEST)
					{
						ce = getContentManager().extractContent(receiMsg);	
						Action act = (Action) ce;
						Order newOrder = (Order) act.getAction();
						
						if(OrderSide.getSide(newOrder).equals(OrderSide.BUY))
						{
							//System.out.println(newOrder);
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
									ACLMessage reply = receiMsg.createReply();
									Action action = new Action(receiMsg.getSender(),tempBuyOrder.poll());
									reply.setPerformative(ACLMessage.INFORM);
									reply.setOntology(ontology.getName());
									reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(reply, action);
									myAgent.send(reply);
								}
								else if(tempBuyOrder.peek().getStatus() == 2)
								{
									ACLMessage reply = receiMsg.createReply();
									Action action = new Action(receiMsg.getSender(),tempBuyOrder.poll());
									reply.setPerformative(ACLMessage.PROPOSE);
									reply.setOntology(ontology.getName());
									reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(reply, action);
									myAgent.send(reply);
								}
								else if(tempBuyOrder.peek().getStatus() == 3)
								{
									ACLMessage reply = receiMsg.createReply();
									Action action = new Action(receiMsg.getSender(),tempBuyOrder.poll());
									reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
									reply.setOntology(ontology.getName());
									reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(reply, action);
									myAgent.send(reply);
								}
							}
						}
						
						else if (OrderSide.getSide(newOrder).equals(OrderSide.SELL))
						{
							//System.out.println(newOrder);
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
									ACLMessage reply = receiMsg.createReply();
									Action action = new Action(receiMsg.getSender(),tempSellOrder.poll());
									reply.setPerformative(ACLMessage.INFORM);
									reply.setOntology(ontology.getName());
									reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(reply, action);
									myAgent.send(reply);
								}
								else if(tempSellOrder.peek().getStatus() == 2)
								{
									ACLMessage reply = receiMsg.createReply();
									Action action = new Action(receiMsg.getSender(),tempSellOrder.poll());
									reply.setPerformative(ACLMessage.PROPOSE);
									reply.setOntology(ontology.getName());
									reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(reply, action);
									myAgent.send(reply);
								}
								else if(tempSellOrder.peek().getStatus() == 3)
								{
									ACLMessage reply = receiMsg.createReply();
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
		});
	}

}
