package orderBookUpdated12;

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
		
		System.out.println("This is updated11 " + getAID().getName());
		
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
							// System.out.println(newOrder);
							// buySideOrder.add(newOrder);
							// BuySideMatch buyOrderMatch = new BuySideMatch();
							// buyOrderMatch.setBQ(buySideOrder);
							// buyOrderMatch.setSQ(sellSideOrder);
							 //System.out.println(buySideOrder);
							 //System.out.println("bid size " +  buySideOrder.size());
							 //buyOrderMatch.matchOrder();
							 ACLMessage reply = receiMsg.createReply();
							 Action action = new Action(receiMsg.getSender(),newOrder);
							 reply.setPerformative(ACLMessage.INFORM);
							 reply.setContent("Buy order is received");
							 reply.setOntology(ontology.getName());
							 reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
							 myAgent.getContentManager().fillContent(reply, action);
							 myAgent.send(reply);
							// System.out.println(reply);
							 //System.out.println("0" + receiMsg);
						}
						
					
						else if (OrderSide.getSide(newOrder).equals(OrderSide.SELL))
						{
							
							 //System.out.println(newOrder);
								//sellSideOrder.add(newOrder);
								//SellSideMatch sellOrderMatch = new SellSideMatch();
								//sellOrderMatch.setBQ(buySideOrder);
								//sellOrderMatch.setSQ(sellSideOrder);
								//System.out.println(sellSideOrder);
								//System.out.println("ask size " + sellSideOrder.size());
								//sellOrderMatch.matchOrder();
							 ACLMessage reply = receiMsg.createReply();
							 Action action = new Action(receiMsg.getSender(),newOrder);
							 reply.setPerformative(ACLMessage.INFORM);
							 reply.setContent("Sell order is received ");
							 reply.setOntology(ontology.getName());
							 reply.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
							 myAgent.getContentManager().fillContent(reply, action);
							 myAgent.send(reply);
							 //System.out.println(reply);
							// System.out.println("1" + receiMsg);
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
			/*	System.out.println("2" + receiMsg);
				receiMsg.setContent(null);
				System.out.println("3" + receiMsg);
				if(receiMsg == null)
				{
					System.out.println("yes");
				}
				else
				{
					System.out.println("no");
				}*/
			}
		});
	}

}
