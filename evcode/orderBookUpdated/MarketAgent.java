package orderBookUpdated51_1;

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
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

public class MarketAgent extends Agent
{
	public static final AID marketAID = new AID("MarketAgent", AID.ISLOCALNAME);
	public static final Ontology ontology = OrderBookOntology.getInstance();
	public static final Codec codecI = new SLCodec();
	public static PriorityQueue<Order> buySideOrders = new PriorityQueue<Order>();
	public static PriorityQueue<Order> sellSideOrders = new PriorityQueue<Order>();
	protected ArrayList investorList = new ArrayList();  
	protected int investorCount = 0;
	
	protected void setup()
	{
		System.out.println("This is updated51 " + getAID().getName());

		getContentManager().registerLanguage(codecI, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(ontology);
			
		InitializeOrder io = new InitializeOrder();
		io.initializeBuyOrder(buySideOrders, sellSideOrders, 100);
			
		System.out.println(getAID().getLocalName() + " LocalBuyOrders: " + buySideOrders.size());
		System.out.println(getAID().getLocalName() + " LocalSellSellOrders: " + sellSideOrders.size());
			
		addBehaviour(new InitOrderbookResponder());
		addBehaviour(new OrderMatchEngine());
		
		this.startInvestors();
	}
	
	private class InitOrderbookResponder extends CyclicBehaviour
	{		
		public void action()
		{
			MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
					MessageTemplate.MatchConversationId("TradingRequest"));
			ACLMessage tradingRequestMsg = receive(pt);

			if(tradingRequestMsg != null)
			{
	            if (tradingRequestMsg.getContent().equals("ReadyToStart"))
	            {
	                investorCount++;
	                System.out.println( "Investors (" + investorCount + " have arrived)" );

	                if (investorCount == 2) 
	                {
	                    System.out.println( "All investors are ready, now start......" );
	                    for (Iterator it = investorList.iterator();  it.hasNext();) 
						{
	                    	ACLMessage replyTradingRequest = new ACLMessage(ACLMessage.AGREE);
	                        replyTradingRequest.setConversationId("TradingPermission");
	                        replyTradingRequest.addReceiver((AID) it.next());
							myAgent.send(replyTradingRequest);
						}							
	                }
	            }
			}
			else
				block();
		}
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
						if(placedOrder.getSide() == 1)
						{
							buySideOrders.add(placedOrder);	
							
							for (Iterator it = investorList.iterator();  it.hasNext();) 
							{   
								Action action = new Action(orderRequestMsg.getSender(), placedOrder);
								ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								replyOrderMsg.addReceiver((AID) it.next());
								myAgent.send(replyOrderMsg);
							}							
							
							ArrayList<Order> tempBuyOrders = new ArrayList<Order>();
							BuySideMatchEngine matchEngine = new BuySideMatchEngine(buySideOrders,sellSideOrders);
							tempBuyOrders.addAll(matchEngine.matchBuyOrders());
							
							int i = 0;
							while(i < tempBuyOrders.size())
							{
								//currentPrice = tempBuyOrder.get(i).getDealingPrice();							
								for (Iterator it = investorList.iterator();  it.hasNext();) 
								{
									Action action = new Action(orderRequestMsg.getSender(), tempBuyOrders.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									replyOrderMsg.addReceiver((AID) it.next());
									myAgent.send(replyOrderMsg);
								}							
								i++;
							}						
						}
						else//placedOrder Side Sell
						{
							sellSideOrders.add(placedOrder);
							
							for (Iterator it = investorList.iterator();  it.hasNext();) 
							{   
								Action action = new Action(orderRequestMsg.getSender(), placedOrder);
								ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
								replyOrderMsg.setOntology(ontology.getName());
								replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
								myAgent.getContentManager().fillContent(replyOrderMsg, action);
								replyOrderMsg.addReceiver((AID) it.next());
								myAgent.send(replyOrderMsg);
							}							
							ArrayList<Order> tempSellOrders = new ArrayList<Order>();
							SellSideMatchEngine matchEngine = new SellSideMatchEngine(sellSideOrders,buySideOrders);
							tempSellOrders.addAll(matchEngine.matchSellOrders());
							
							int i = 0;
							while(i < tempSellOrders.size())
							{
								//currentPrice = tempBuyOrder.get(i).getDealingPrice();							
								for (Iterator it = investorList.iterator();  it.hasNext();) 
								{
									Action action = new Action(orderRequestMsg.getSender(), tempSellOrders.get(i));
									ACLMessage replyOrderMsg = new ACLMessage(ACLMessage.INFORM);
									replyOrderMsg.setOntology(ontology.getName());
									replyOrderMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
									myAgent.getContentManager().fillContent(replyOrderMsg, action);
									replyOrderMsg.addReceiver((AID) it.next());
									myAgent.send(replyOrderMsg);
								}							
								i++;
							}						
						}
					}
					else if(orderRequestMsg.getPerformative() == ACLMessage.CANCEL)
					{
						placedOrder.setStatus(4);
						
						if(placedOrder.getSide() == 1)
				        {
							placedOrder.cancelFrom(buySideOrders);
				        }
						else
						{
							placedOrder.cancelFrom(sellSideOrders);
						}
						for (Iterator it = investorList.iterator();  it.hasNext();) 
						{
							Action action = new Action(orderRequestMsg.getSender(),placedOrder);
							ACLMessage replyCancelMsg = new ACLMessage(ACLMessage.CONFIRM);
							replyCancelMsg.setOntology(ontology.getName());
							replyCancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
							myAgent.getContentManager().fillContent(replyCancelMsg, action);
							replyCancelMsg.addReceiver((AID) it.next());
							myAgent.send(replyCancelMsg);
						}							
					}
					//System.out.println(getAID().getLocalName() + " BuyOrders: " + buySideOrders);
					//System.out.println(getAID().getLocalName() + " SellSellOrders: " + sellSideOrders);
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
    	PlatformController container = getContainerController(); // get a container controller for creating new agents
    	try 
    	{
    		String investorI = "Ev";
    		String investorII = "Peter";
		    AgentController investorContrallerI = container.createNewAgent(investorI, "orderBookUpdated51_1.InvestorAgent", null);
		    AgentController investorContrallerII = container.createNewAgent(investorII, "orderBookUpdated51_1.InvestorAgentII", null);
		    investorContrallerI.start();
		    investorContrallerII.start();
  
		    investorList.add(new AID(investorI, AID.ISLOCALNAME));
		    investorList.add(new AID(investorII, AID.ISLOCALNAME));            
        }
        catch (Exception e) {
            System.err.println( "Exception while adding investors: " + e );
            e.printStackTrace();
        }
    }
}

