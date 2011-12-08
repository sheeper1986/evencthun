package orderBookUpdated15;

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
		
		System.out.println("This is updated15 " + getAID().getName());
		//ParallelBehaviour pb = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ANY);
		//pb.addSubBehaviour();
		addBehaviour(new RandomGenerator(this, 5000));
		addBehaviour(new MessageManager());
	       
           
	 }
	
	private class RandomGenerator extends TickerBehaviour
	{	
		public RandomGenerator(Agent a, long period) 
		{
			super(a, period);
			
		}

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
				newOrder.setType(1);
				
				if(newOrder.getType() == 1)
				{
					newOrder.setOrderID(id++);
					newOrder.setSymbol("GOOGLE");
					newOrder.setSide(1);
					newOrder.setVolume(randomVolume);
					newOrder.setOpenTime(System.nanoTime());
				}
				else if(newOrder.getType() == 2)
				{
					newOrder.setOrderID(id++);
					newOrder.setSymbol("GOOGLE");
					newOrder.setSide(1);
					newOrder.setVolume(randomVolume);
					newOrder.setPrice(randomPrice);
					newOrder.setOpenTime(System.nanoTime());
					//loList.add(newOrder);
				}
				System.out.println(newOrder);
				
				
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
					if(receiMsgFromEx.getPerformative() == ACLMessage.INFORM)
					{
						ce = getContentManager().extractContent(receiMsgFromEx);	
						Action act = (Action) ce;
						Order replyOrder = (Order) act.getAction();
						System.out.println("Received !!!!!" + replyOrder + " " + getAID().getName());
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
			else
				block();
		}
	}
}
