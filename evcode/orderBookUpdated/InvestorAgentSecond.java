package orderBookUpdated50_2;

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

public class InvestorAgentSecond extends Agent
{
	private Strategy tradeStrategy = new Strategy();
	public static ArrayList<Order> pendingOrderListII = new ArrayList<Order>();
	//private ArrayList<Asset> assetList = new ArrayList<Asset>();
	//private Asset asset = new Asset();
	//private double fundAvailable;
	//private int id = 0;
	private LinkedList<Order> buySideOrdersII = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrdersII = new LinkedList<Order>();
	
	protected void setup()
	{
		getContentManager().registerLanguage(MarketAgent.codecI, FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MarketAgent.ontology);
		
		System.out.println("This is updated50 " + getAID().getName());
		//ParallelBehaviour pb = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ANY);
		//pb.addSubBehaviour();
		addBehaviour(new InitOrderbookRequest());
		addBehaviour(new ProcessedOrderManager());
		addBehaviour(new RandomTradingBehaviourII(this, 2000));
		
		//addBehaviour(new PriceChecker(this, 1000));
		//addBehaviour(new AutoCancel());
	 }
	

	private class  InitOrderbookRequest extends OneShotBehaviour
	{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage initializeOrdersMsg = new ACLMessage(ACLMessage.REQUEST);
			initializeOrdersMsg.setConversationId("InitializeOrders");
			initializeOrdersMsg.addReceiver(MarketAgent.marketAID);
			myAgent.send(initializeOrdersMsg);	
		}
		
	}
	private class ProcessedOrderManager extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(MarketAgent.ontology.getName())); 
			//blockingReceive() cannot use here, because it keeps messages, cyclicBehaviour will not stop
			ACLMessage processedOrderMsg = receive(mt);
		
			if(processedOrderMsg!=null){
				try
				{
					ContentElement ce = null;
					ce = getContentManager().extractContent(processedOrderMsg);	
					Action act = (Action) ce;
					Order processedOrder = (Order) act.getAction();
					if(processedOrderMsg.getPerformative() == ACLMessage.AGREE)
					{
						if(processedOrder.isBuySide())
						{
							buySideOrdersII.add(processedOrder);
							Collections.sort(buySideOrdersII);
						}
						else
						{
							sellSideOrdersII.add(processedOrder);
							Collections.sort(sellSideOrdersII);
						}
						System.out.println("LocalBuy~~~~~~ II" + buySideOrdersII);
						System.out.println("LocalSell~~~~~~II " + sellSideOrdersII);
					}
					else if(processedOrderMsg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
					{
						System.out.println("Filled !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
						//asset.updateAssetList(assetList, processedOrder);
					}
					
					else if(processedOrderMsg.getPerformative() == ACLMessage.INFORM)
					{
						System.out.println("Great PartlyFilled !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
						//asset.updateAssetList(assetList, processedOrder);
					}
					
					else if(processedOrderMsg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
					{
						System.out.println("Rejected !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
					}
					
					else if(processedOrderMsg.getPerformative() == ACLMessage.CONFIRM)
					{
						System.out.println("Cancel Successful !" + processedOrder);
						processedOrder.updatePendingOrderList(pendingOrderListII);
					}
					
					System.out.println("Updated Pending List II" + pendingOrderListII);
					//System.out.println("Market Price: " + MarketAgent.currentPrice);
					//System.out.println("Updated Asset List " + assetList);
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
	
	/*private class PriceChecker extends TickerBehaviour
	{
		public PriceChecker(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick()
		{
			try
			{
				ACLMessage checkPriceMsg = new ACLMessage(ACLMessage.REQUEST);
				checkPriceMsg.setConversationId("CheckPrice");
				checkPriceMsg.addReceiver(MarketAgent.marketAID);
				myAgent.send(checkPriceMsg);	
			}
			catch(Exception ex){
				System.out.println(ex);
			}
		}
	}*/
	
	/*private class AutoCancel extends CyclicBehaviour
	{
		public void action() 
		{
			//MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchConversationId("PriceInform"));
			//ACLMessage receiPrice = receive(pt);
			
			//if(receiPrice != null)
			//{
				double marketPrice = MarketAgent.currentPrice;//Double.parseDouble(receiPrice.getContent());
				//System.out.println(marketPrice);
				if(pendingOrderList.size()>5 && marketPrice != 0)
				{
					ArrayList<Order> temp = new ArrayList();
					temp.addAll(tradeStrategy.matchedOrderSpread(pendingOrderList, marketPrice, 3));
					
					int i = 0;
					while (i < temp.size())
					{
						try
						{
							Action cancelAct = new Action(MarketAgent.marketAID, temp.get(i));
							ACLMessage cancelMsg = new ACLMessage(ACLMessage.CANCEL);
							cancelMsg.addReceiver(MarketAgent.marketAID);
							cancelMsg.setOntology(MarketAgent.ontology.getName());
							cancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
                            myAgent.getContentManager().fillContent(cancelMsg, cancelAct);
							myAgent.send(cancelMsg);
							//temp.remove(i);
							i++;
						 }
						catch (CodecException e){
							 e.printStackTrace();
							 } 
						catch (OntologyException e){
								e.printStackTrace();
								}	
					 }
					block();	
				}
				else
					block();
			//}
			//else
			//	block();	
		}
	}
	*/
}