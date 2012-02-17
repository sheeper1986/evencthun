package orderBookUpdated52_5;

import java.util.ArrayList;
import java.util.LinkedList;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LocalOrderManager extends CyclicBehaviour
{
	private LinkedList<Order> buySideOrders;
	private LinkedList<Order> sellSideOrders;
	private ArrayList<Order> pendingOrderList;
	
	public LocalOrderManager(LinkedList<Order> buySideOrders, LinkedList<Order> sellSideOrders, ArrayList<Order> pendingOrderList) 
	{
		this.buySideOrders = buySideOrders;
		this.sellSideOrders = sellSideOrders;
		this.pendingOrderList = pendingOrderList;
	}
	public void action()
	{
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), 
				MessageTemplate.MatchOntology(MarketAgent.ontology.getName())); 
		//blockingReceive() cannot use here, because it keeps messages, cyclicBehaviour will not stop
		ACLMessage processedOrderMsg = myAgent.receive(mt);

		if(processedOrderMsg != null)
		{
			try
			{
				ContentElement ce = null;
			    ce = myAgent.getContentManager().extractContent(processedOrderMsg);	
			    Action act = (Action) ce;
			    Order orderInfomation = (Order) act.getAction();
			    //System.out.println(orderInfomation);
			    
			    if(processedOrderMsg.getPerformative() == ACLMessage.INFORM)
			    {
			    	new ManageOrders(orderInfomation).updateLocalOrderbook(buySideOrders, sellSideOrders);
			    	
			    	if(orderInfomation.getOrderID().contains(myAgent.getLocalName()))
			    	{
			    		new ManageOrders(orderInfomation).updatePendingOrderList(pendingOrderList);
				    	//System.out.println(myAgent.getLocalName() + " Updated Pending List " + pendingOrderList);
			    	}
			    }
			    	//System.out.println(myAgent.getLocalName() + " BuyOrders: " + buySideOrders.size());
			    	//System.out.println(myAgent.getLocalName() + " SellOrders: " + sellSideOrders.size());
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
