package orderBookUpdated52_5;

import java.util.*;

import eugene.market.ontology.field.Side;
import examples.party.HostUIFrame;

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
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

public class MarketAgent extends Agent
{
	public static final AID marketAID = new AID("MarketAgent", AID.ISLOCALNAME);
	public static final Ontology ontology = OrderBookOntology.getInstance();
	public static final Codec codec = new SLCodec();
	public static PriorityQueue<Order> buySideQueue = new PriorityQueue<Order>();
	public static PriorityQueue<Order> sellSideQueue = new PriorityQueue<Order>();
	protected ArrayList<AID> investorList = new ArrayList<AID>();  
	private ArrayList<Order> executedOrders = new ArrayList<Order>();
	private int idOfInvestor = 0;
	public static final int NUMBER_OF_NOISETRADER = 3;
	
	protected void setup()
	{
		System.out.println("This is updated52_5 " + getAID().getName());

		getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
			
		new InitializeOrder().initOrderbook(buySideQueue, sellSideQueue, 1000);
			
		System.out.println(getLocalName() + " LocalBuyOrders: " + buySideQueue.size());
		System.out.println(getLocalName() + " LocalSellOrders: " + sellSideQueue.size());
			
		addBehaviour(new InitOrderbookResponder(investorList));
		addBehaviour(new OrderMatchEngine());
		addBehaviour(new ExecutedOrdersReport(this, 1000*361, executedOrders));
		
		this.startInvestors();
	}
	
	private class OrderMatchEngine extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), 
					MessageTemplate.MatchOntology(ontology.getName())); 
			ACLMessage orderRequestMsg = receive(mt);
			
			if(orderRequestMsg != null)
			{
				try
				{
					ContentElement ce = null;
					ce = getContentManager().extractContent(orderRequestMsg);	
					Action act = (Action) ce;
					Order placedOrder = (Order) act.getAction();
					
					if(orderRequestMsg.getPerformative() == ACLMessage.CFP)
					{	
						if(placedOrder.isBuySide())
						{
							buySideQueue.add(placedOrder);	
							if(placedOrder.isLimitOrder())
							{
								for (Iterator it = investorList.iterator();  it.hasNext();) 
								{   
									Action action = new Action(orderRequestMsg.getSender(), placedOrder);
									ACLMessage replyOrderMsg = new Messages(ACLMessage.INFORM,(AID) it.next()).createMessage();
									//ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									//replyOrderMsg.setOntology(ontology.getName());
									//replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									//replyOrderMsg.addReceiver((AID) it.next());
									myAgent.send(replyOrderMsg);
								}							
							}
							
							ArrayList<Order> tempBuyOrders = new ArrayList<Order>();
							BuySideMatchEngine matchEngine = new BuySideMatchEngine(buySideQueue,sellSideQueue);
							
							tempBuyOrders.addAll(matchEngine.matchBuyOrders());
							
							//System.out.println(getAID().getLocalName() + " BuyOrders: " + buySideQueue.size());
							//System.out.println(getAID().getLocalName() + " SellOrders: " + sellSideQueue.size());
							
							int i = 0;
							while(i < tempBuyOrders.size())
							{
								//if(tempBuyOrders.get(i).isLimitOrder())
								//{
									executedOrders.add(tempBuyOrders.get(i));
								//}
								
								for (Iterator it = investorList.iterator();  it.hasNext();) 
								{
									Action action = new Action(orderRequestMsg.getSender(), tempBuyOrders.get(i));
									ACLMessage replyOrderMsg = new Messages(ACLMessage.INFORM,(AID) it.next()).createMessage();
									//ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									//replyOrderMsg.setOntology(ontology.getName());
									//replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									//replyOrderMsg.addReceiver((AID) it.next());
									myAgent.send(replyOrderMsg);
								}							
								i++;
							}						
						}
						else//placedOrder Side Sell
						{
							sellSideQueue.add(placedOrder);
							if(placedOrder.isLimitOrder())
							{
								for (Iterator it = investorList.iterator();  it.hasNext();) 
								{   
									Action action = new Action(orderRequestMsg.getSender(), placedOrder);
									ACLMessage replyOrderMsg = new Messages(ACLMessage.INFORM,(AID) it.next()).createMessage();
									//ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									//replyOrderMsg.setOntology(ontology.getName());
									//replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									//replyOrderMsg.addReceiver((AID) it.next());
									myAgent.send(replyOrderMsg);
								}							
							}
							
							ArrayList<Order> tempSellOrders = new ArrayList<Order>();
							SellSideMatchEngine matchEngine = new SellSideMatchEngine(sellSideQueue,buySideQueue);
							
							tempSellOrders.addAll(matchEngine.matchSellOrders());
							
							//System.out.println(getAID().getLocalName() + " BuyOrders: " + buySideQueue.size());
							//System.out.println(getAID().getLocalName() + " SellOrders: " + sellSideQueue.size());
							
							int i = 0;
							while(i < tempSellOrders.size())
							{
								//if(tempSellOrders.get(i).isLimitOrder())
								//{
									executedOrders.add(tempSellOrders.get(i));
								//}
															
								for (Iterator it = investorList.iterator();  it.hasNext();) 
								{
									Action action = new Action(orderRequestMsg.getSender(), tempSellOrders.get(i));
									//ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									ACLMessage replyOrderMsg = new Messages(ACLMessage.INFORM,(AID) it.next()).createMessage();
									//replyOrderMsg.setOntology(ontology.getName());
									//replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									//replyOrderMsg.addReceiver((AID) it.next());
									myAgent.send(replyOrderMsg);
								}							
								i++;
							}						
						}
					}
					else if(orderRequestMsg.getPerformative() == ACLMessage.CANCEL)
					{
						placedOrder.setStatus(4);
						executedOrders.add(placedOrder);
						new ManageOrders(placedOrder).cancelFrom(buySideQueue, sellSideQueue);
				        
						for (Iterator it = investorList.iterator();  it.hasNext();) 
						{
							Action action = new Action(orderRequestMsg.getSender(),placedOrder);
							//ACLMessage replyCancelMsg = new ACLMessage(ACLMessage.INFORM);
							ACLMessage replyCancelMsg = new Messages(ACLMessage.INFORM,(AID) it.next()).createMessage();
							//replyCancelMsg.setOntology(ontology.getName());
							//replyCancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
							myAgent.getContentManager().fillContent(replyCancelMsg, action);
							//replyCancelMsg.addReceiver((AID) it.next());
							myAgent.send(replyCancelMsg);
						}
						//System.out.println(getAID().getLocalName() + " BuyOrders: " + buySideQueue);
						//System.out.println(getAID().getLocalName() + " SellOrders: " + sellSideQueue);
					}
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
	
    protected void startInvestors() 
    {
    	AgentContainer c = getContainerController();; // get a container controller for creating new agents
    	
    	try 
    	{
    		String vwapTrader = "VWAPTrader";
    		for(int i=0; i<NUMBER_OF_NOISETRADER; i++)
    		{
    			String localName = "NoiseTrader" + i;
    			AgentController investorContraller = c.createNewAgent(localName, "orderBookUpdated52_5.NoiseTrader", null);
    			investorList.add(new AID(localName, AID.ISLOCALNAME));
    			investorContraller.start();
    		}
		    AgentController investorContrallerIV = c.createNewAgent(vwapTrader, "orderBookUpdated52_5.VWAPTrader", null);
		    investorContrallerIV.start();

		    investorList.add(new AID(vwapTrader, AID.ISLOCALNAME));  
        }
        catch (Exception e) {
            System.err.println( "Exception while adding investors: " + e );
            e.printStackTrace();
        }
    }
}

