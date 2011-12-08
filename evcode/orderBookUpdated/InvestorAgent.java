package orderBookUpdated12;

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
	private ArrayList<Order> loList = new ArrayList<Order>();
	
	protected void setup()
	{
		
		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
		
		System.out.println("This is updated11" + getAID().getName());
		
		//ParallelBehaviour pb = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ANY);
		//pb.addSubBehaviour
		addBehaviour(new TickerBehaviour(this, 5000){
			
			protected void onTick()
			{
				int randomVolume = (int)(10+Math.random()*90);
				int randomSide = (int)(1+Math.random()*2);
				int randomTime = (int)(1000 + Math.random()*4000);
				int randomType = (int)(1+Math.random()*2);
				int randomPrice = (int)(50+Math.random()*5);
				
			    Order newOrder = new Order();
				try
				{
					newOrder.setType(randomType);
					
					if(newOrder.getType() == 1)
					{
						newOrder.setOrderID(id++);
						newOrder.setSymbol("GOOGLE");
						newOrder.setSide(randomSide);
						newOrder.setVolume(randomVolume);
						newOrder.setOpenTime(System.nanoTime());
					}
					else if(newOrder.getType() == 2)
					{
						newOrder.setOrderID(id++);
						newOrder.setSymbol("GOOGLE");
						newOrder.setSide(randomSide);
						newOrder.setVolume(randomVolume);
						newOrder.setPrice(randomPrice);
						newOrder.setOpenTime(System.nanoTime());
						//loList.add(newOrder);
					}
					
					
					Action act = new Action(CentralisedAgent, newOrder);
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					
					msg.addReceiver(CentralisedAgent);
					msg.setOntology(ontology.getName());
					msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					myAgent.getContentManager().fillContent(msg, act);
					
					//System.out.println(msg);
					myAgent.send(msg);	
					//myAgent.addBehaviour(new RequestPerformer());
					reset(randomTime);
				}
				catch(Exception ex)
				{
						System.out.println(ex);
				}
				addBehaviour(new MessageManager());
			}
		});
	       
           
	 }
	private class MessageManager extends Behaviour{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and( MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(ontology.getName()) ); 
			ACLMessage receiMsgFromEx = blockingReceive(mt);
			
			try
			{
				ContentElement ce = null;
				if(receiMsgFromEx.getPerformative() == ACLMessage.INFORM)
				{
					ce = getContentManager().extractContent(receiMsgFromEx);	
					Action act = (Action) ce;
					Order replyOrder = (Order) act.getAction();
					System.out.println("Received !!!!!" + replyOrder);
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
			catch(Exception e)
			{
				System.out.println(e);
			}
	}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return true;
		}

}
}
